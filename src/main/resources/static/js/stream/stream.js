/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

var ws = new WebSocket('ws://' + location.host + '/live');
var rec = new WebSocket('ws://' + location.host + '/record');
var live;
var watch;
var webRtcPeer;
var webRtcRecord;
var state;

const NO_CALL = 0;
const IN_CALL = 1;
const POST_CALL = 2;
const DISABLED = 3;
const IN_PLAY = 4;

window.onload = function() {
    live = document.getElementById('live-video');
    watch = document.getElementById('live');

    // 시청모드이면 접속시 viewer 함수 실행
    if (watch) {
        viewer();
    }

    // streamInfo == null이면(등록된 방송 정보가 없다면) 방송 시작, 방송 종료 버튼 비활성화
    if (!streamInfo) {
        setState(DISABLED);
    } else {    // 기존에 등록된 방송 정보가 있다면 방송 시작 버튼 활성화, 방송 종료 버튼 비활성화
        setState(NO_CALL);
    }

}

window.onbeforeunload = function() {
    ws.close();
    rec.close();
}

function setState(nextState) {
    switch (nextState) {
        case NO_CALL:
            $('#start').attr('disabled', false);
            $('#stop').attr('disabled', true);
            break;
        case DISABLED:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', true);
            break;
        case IN_CALL:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', false);
            break;
        case POST_CALL:
            $('#start').attr('disabled', false);
            $('#stop').attr('disabled', true);
            break;
        case IN_PLAY:
            $('#start').attr('disabled', true);
            $('#stop').attr('disabled', false);
            break;
        default:
            onError('Unknown state ' + nextState);
            return;
    }
    state = nextState;
}

ws.onmessage = function(message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    switch (parsedMessage.id) {
        case 'presenterResponse':
            presenterResponse(parsedMessage);
            break;
        case 'viewerResponse':
            viewerResponse(parsedMessage);
            break;
        case 'iceCandidate':
            webRtcPeer.addIceCandidate(parsedMessage.candidate, function(error) {
                if (error)
                    return console.error('Error adding candidate: ' + error);
            });
            break;
        case 'stopCommunication':
            dispose();
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
}

rec.onmessage = function(message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    switch (parsedMessage.id) {
        case 'startResponse':
            startResponse(parsedMessage);
            break;
        case 'stop':
            dispose();
            break;
        case 'iceCandidate':
            webRtcRecord.addIceCandidate(parsedMessage.candidate, function(error) {
                if (error)
                    return console.error('Error adding candidate: ' + error);
            });
            break;
        case 'recording':
            break;
        case 'stopped':
            uploadFileToNCP();
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
}

function presenterResponse(message) {
    setState(IN_CALL);
    console.log('SDP answer received from server. Processing ...');

    webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
        if (error) {
            reject(error);
        }
        createVideo();
    })
}

function startResponse(message) {
    console.log('SDP answer received from server. Processing ...');
    webRtcRecord.processAnswer(message.sdpAnswer, function(error) {
        if (error)
            return console.error(error);
    });
}

function viewerResponse(message) {
    if (message.response != 'accepted') {
        var errorMsg = message.message ? message.message : 'Unknown error';
        console.info('Call not accepted for the following reason: ' + errorMsg);
        dispose();
    } else {
        webRtcPeer.processAnswer(message.sdpAnswer, function(error) {
            if (error)
                return console.error(error);
        });
    }
}

function startLive() {
    axios.post("/stream/start", {
        streamNo: streamNo
    })
        .then((response) => {
            console.log(response);

            const data = response.data;

            const productElement = document.querySelector(".product-element");
            document.getElementById("broadcastTitle").value = data.streamTitle;
            document.getElementById("broadcastDesc").value = data.streamDescription;

            productElement.innerHTML = `
                <img src="/img/product_img/${data.productImg}" alt="상품 이미지" class="product-img">
                <div class="product-info" id="${data.productNo}">
                    <p class="product-name">${data.productName}</p>
                    <div class="product-price-container">
                        <p class="product-origin-price">${data.productPrice}</p>
                    </div>
                </div>
            `

            document.getElementById("discountRate").value = data.productSale;

            if (!webRtcPeer) {
                showSpinner(live);

                let options = {
                    localVideo: live,
                    onicecandidate: onLiveIceCandidate
                }
                webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
                    function (error) {
                        if (error) {
                            return console.error(error);
                        }
                        webRtcPeer.generateOffer(onLiveOffer);
                    });
            }
        })
        .catch((error) => {
            console.log(error);
        });
}

function viewer() {
    if (!webRtcPeer) {
        showSpinner(watch);

        var options = {
            remoteVideo : watch,
            onicecandidate : onLiveIceCandidate
        }
        webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
            function(error) {
                if (error) {
                    return console.error(error);
                }
                this.generateOffer(onViewOffer);
            });
    }
}

function onLiveIceCandidate(candidate) {
    console.log("Local candidate" + JSON.stringify(candidate));

    let message = {
        id : 'onIceCandidate',
        candidate : candidate
    };
    sendLiveMessage(message);
}

function createVideo() {
    console.log('Starting video call ...');

    let options = {
        localVideo : live,
        mediaConstraints : getConstraints(),
        onicecandidate : onRecordIceCandidate
    }

    webRtcRecord = new kurentoUtils.WebRtcPeer.WebRtcPeerSendrecv(options,
                function(error) {
            if (error)
                return console.error(error);
            webRtcRecord.generateOffer(onRecordOffer);
        });
}

function onRecordIceCandidate(candidate) {
    console.log("Local candidate" + JSON.stringify(candidate));

    let message = {
        id : 'onIceCandidate',
        candidate : candidate
    };
    sendRecordMessage(message);
}

function onLiveOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    let message = {
        id : 'presenter',
        sdpOffer : offerSdp
    }
    sendLiveMessage(message);
}

function onViewOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    var message = {
        id : 'viewer',
        sdpOffer : offerSdp
    }
    sendLiveMessage(message);
}

function onRecordOffer(error, offerSdp) {
    if (error)
        return console.error('Error generating the offer');
    console.info('Invoking SDP offer callback function ' + location.host);
    let message = {
        id : 'start',
        sdpOffer : offerSdp,
        mode :  $('input[name="mode"]:checked').val()
    }
    sendRecordMessage(message);
}

function getConstraints() {
    return {
        audio: true,
        video: true
    };
}

function stopLive() {
    let message = {
        id : 'stop'
    }
    sendLiveMessage(message);
    dispose();
}

function stopRecord() {
    let stopMessageId = (state === IN_CALL) ? 'stop' : 'stopPlay';
    console.log('Stopping video while in ' + state + '...');
    if (webRtcRecord) {
        webRtcRecord.dispose();
        webRtcRecord = null;
        setState(NO_CALL);

        let message = {
            id : stopMessageId
        }
        sendRecordMessage(message);
    }
    hideSpinner(live);
}

function dispose() {
    if (webRtcPeer) {
        webRtcPeer.dispose();
        webRtcPeer = null;
    }
    stopRecord();
}

function sendLiveMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Sending message: ' + jsonMessage);
    ws.send(jsonMessage);
}

function sendRecordMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Sending message: ' + jsonMessage);
    rec.send(jsonMessage);
}

function showSpinner() {
    for (let i = 0; i < arguments.length; i++) {
        arguments[i].poster = '/img/transparent-1px.png';
        arguments[i].style.background = 'center transparent url("./img/spinner.gif") no-repeat';
    }
}

function hideSpinner() {
    for (let i = 0; i < arguments.length; i++) {
        arguments[i].src = '';
        arguments[i].poster = '/img/webrtc.png';
        arguments[i].style.background = '';
    }
}

function onError(error) {
    console.error(error);
}

function uploadFileToNCP() {
    let title = 'test.mp4';
    axios.post('/stream/vod/upload', {
        title
    })
        .then(response => {
            console.log("응답 데이터: ", response.data)
        })
        .catch(error => {
            console.log("에러 발생: ", error);
        })
}

/**
 * Lightbox utility (to display media pipeline image in a modal dialog)
 */
$(document).delegate('*[data-toggle="lightbox"]', 'click', function(event) {
    event.preventDefault();
    $(this).ekkoLightbox();
});
