document.addEventListener("DOMContentLoaded", function () {
    setupFilterButtons();                      // 필터링 버튼 셋팅
    loadLive();                                // 라이브 방송 불러오기
    loadStandBy(0, 'none');     // 준비중 방송 불러오기
    loadVod(0);                        // 최초 1페이지의 VOD 목록 불러오기
});

// 정렬 버튼 클릭 시
function setupFilterButtons() {
    setupLiveFilterButtons();
    setupVodFilterButtons();
}

function setupLiveFilterButtons() {
    const buttons = document.querySelectorAll('.live-filter-btn');
    buttons.forEach(button => {
        button.addEventListener('click', function () {
            // 클릭된 버튼에 'selected' 클래스를 추가하고 나머지 버튼에서 제거
            buttons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');

            const liveGrid = document.getElementById('live-grid');
            liveGrid.innerHTML = '';
        });
    });
}

function setupVodFilterButtons() {
    axios.get('/api/categories')
        .then((response) => {
            const filterButtons = document.getElementById('vod-buttons');
            const categories = response.data;

            const allButton = document.createElement("button");

            allButton.className = "vod-filter-btn";
            allButton.id = `filter-all`;
            allButton.textContent = '전체';

            allButton.addEventListener('click', function() {
                loadVod(0);
            });
            filterButtons.appendChild(allButton);

            categories.forEach(category => {
                // 새 버튼 요소 생성
                const button = document.createElement("button");

                // 클래스와 id, 그리고 버튼 텍스트 설정
                button.className = "vod-filter-btn";
                button.id = `filter-${category.categoryNo}`;
                button.textContent = category.categoryName;

                button.addEventListener('click', function() {
                    loadVodByCategory(parseInt(category.categoryNo), 0);
                });

                filterButtons.appendChild(button);
            });

            const buttons = document.querySelectorAll('.vod-filter-btn');
            buttons.forEach(button => {
                button.addEventListener('click', function () {
                    // 클릭된 버튼에 'selected' 클래스를 추가하고 나머지 버튼에서 제거
                    buttons.forEach(btn => btn.classList.remove('selected'));
                    button.classList.add('selected');

                    const vodGrid = document.getElementById('vod-grid');
                    vodGrid.innerHTML = '';
                });
            })
        });

}

function filterAll() {
    loadLive();
    loadStandBy(0, 'none');
}

function filterLive() {
    loadLive();
}

function filterStandBy() {
    loadStandBy(0, 'filtered');
}

function loadLive() {
    axios.get('/stream/live')
        .then(response => {
            const live = response.data['live'];
            const liveGrid = document.getElementById('live-grid');
            liveGrid.innerHTML = '';

            if (!live) {
                liveGrid.innerHTML = '<p>진행중인 라이브가 없습니다.</p>';
            } else {
                const liveDiv = document.createElement('div');
                liveDiv.classList.add('item');
                const productPrice = live.productPrice;
                const discountRate = live.productSale;
                const streamStatus = live.streamStatus;
                const streamStartTime = live.streamStartTime;

                const discountedPrice = Math.floor(productPrice * ((100 - discountRate) / 100));

                const formattedOriginPrice = productPrice.toLocaleString('ko-KR');
                const formattedDiscountedPrice = discountedPrice.toLocaleString('ko-KR');

                const date = new Date(streamStartTime);

                // 년, 월, 일을 추출하여 포맷
                const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

                liveDiv.innerHTML = `
                    <div class="stream-img-container">
                        <img id="live-icon" src="/img/icon/live.png" alt="product-live" class="live-icon" style="width: 50px" />
                        <img src="${live.productImg}" alt="${live.productName}" />
                    </div>
                    <p id="title">${live.streamTitle}</p>
                    <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedOriginPrice}원</p>
                    <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${live.productSale} %</p>
                    <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedDiscountedPrice}원</p>
                `;

                if (discountRate === 0) {
                    liveDiv.querySelector(".product-sale").style.display = "none";
                    liveDiv.querySelector("#product-sale-percent").style.display = "none";
                } else {
                    liveDiv.querySelector(".product-sale").style.display = "block";
                    liveDiv.querySelector("#product-sale-percent").style.display = "block";
                }

                if (streamStatus === 'ONAIR') {
                    liveDiv.querySelector("#live-icon").style.display = "block";
                } else {
                    liveDiv.querySelector("#live-icon").style.display = "none";
                }

                // 라이브 클릭 시 시청 및 상세 페이지로 이동
                liveDiv.addEventListener('click', () => {
                    window.location.href = `/webrtc/watch/${live.streamNo}`;
                });
                liveGrid.appendChild(liveDiv);
            }
        })
        .catch(error => {
            console.error("라이브 목록을 불러오는 중 오류 발생: ", error);
        });
}

function loadStandBy(pageNo, option) {
    axios.get('/stream/standby/list', {
        params: {
            pageNo: pageNo,
            option: option
        }
    })
        .then(response => {
            const pageInfo = response.data['pageInfo'];
            const standByContent = pageInfo['content'];
            const liveGrid = document.getElementById('live-grid');

            standByContent.forEach(standBy => {
                const standByDiv = document.createElement('div');
                standByDiv.classList.add('item');
                const productPrice = standBy.productPrice;
                const discountRate = standBy.productSale;
                const discountedPrice = Math.floor(productPrice * ((100 - discountRate) / 100));

                const formattedOriginPrice = productPrice.toLocaleString('ko-KR');
                const formattedDiscountedPrice = discountedPrice.toLocaleString('ko-KR');

                standByDiv.innerHTML = `
                            <div class="stream-img-container">
                                <img src="${standBy.productImg}" alt="${standBy.productName}" />
                            </div>
                            <p id="title">${standBy.streamTitle}</p>
                            <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedOriginPrice}원</p>
                            <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${standBy.productSale} %</p>
                            <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedDiscountedPrice}원</p>
                        `;

                if (discountRate === 0) {
                    standByDiv.querySelector(".product-sale").style.display = "none";
                    standByDiv.querySelector("#product-sale-percent").style.display = "none";
                } else {
                    standByDiv.querySelector(".product-sale").style.display = "block";
                    standByDiv.querySelector("#product-sale-percent").style.display = "block";
                }

                // VOD 클릭 시 상세 및 시청 페이지로 이동
                standByDiv.addEventListener('click', () => {
                    Swal.fire({
                        icon: 'error',
                        title: '시청 불가',
                        text: '준비중인 라이브입니다'
                    });
                });

                liveGrid.appendChild(standByDiv);
            });

            renderPageContent(pageInfo, 'live-page-container', option);

        })
        .catch(error => {
            console.error("준비중인 라이브 목록을 불러오는 중 오류 발생: ", error);
        })
}

function loadVod(pageNo) {
    // 현재 페이지의 VOD 목록 불러오기
    axios.get('/stream/vod/list/page', {
        params: {
            pageNo: pageNo
        }
    }).then(response => {
            const pageInfo = response.data['pageInfo'];
            const vodContent = pageInfo['content'];
            const vodGrid = document.getElementById('vod-grid');

            vodContent.forEach(vod => {
                const vodDiv = document.createElement('div');
                vodDiv.classList.add('item');
                const productPrice = vod.productPrice;
                const discountRate = vod.productSale;
                const streamStartTime = vod.streamStartTime;

                const discountedPrice = Math.floor(productPrice * ((100 - discountRate) / 100));

                const formattedOriginPrice = productPrice.toLocaleString('ko-KR');
                const formattedDiscountedPrice = discountedPrice.toLocaleString('ko-KR');

                const date = new Date(streamStartTime);

                // 년, 월, 일을 추출하여 포맷
                const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

                vodDiv.innerHTML = `
                    <div class="stream-img-container">
                        <img src="${vod.productImg}" alt="${vod.productName}" />
                    </div>
                    <p id="date">${formattedDate}</p>
                    <p id="title">${vod.streamTitle}</p>
                    <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedOriginPrice}원</p>
                    <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${vod.productSale} %</p>
                    <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedDiscountedPrice}원</p>
                `;

                if (discountRate === 0) {
                    vodDiv.querySelector(".product-sale").style.display = "none";
                    vodDiv.querySelector("#product-sale-percent").style.display = "none";
                } else {
                    vodDiv.querySelector(".product-sale").style.display = "block";
                    vodDiv.querySelector("#product-sale-percent").style.display = "block";
                }

                // VOD 클릭 시 상세 및 시청 페이지로 이동
                vodDiv.addEventListener('click', () => {
                    window.location.href = `/watch/vod/${vod.streamNo}`;
                });

                vodGrid.appendChild(vodDiv);
            });

            renderPageContent(pageInfo, 'vod-page-container');
        })
        .catch(error => {
            console.error("VOD 목록을 불러오는 중 오류 발생:", error);
        });
}

function renderPageContent(pageInfo, containerName, option) {
    // 페이지 버튼 영역 생성
    const pageContainer = document.getElementById(containerName);
    pageContainer.innerHTML = '';
    const totalPages = pageInfo['totalPages'];
    const pageNumber = pageInfo['number'];

    if (pageNumber !== totalPages - 1) {
        const pageDiv = document.createElement('div');
        pageDiv.classList.add('page-item');
        pageDiv.innerHTML = `<button class="load-more">더보기</button>`

        // 페이지 번호 클릭시 해당 페이지의 VOD 목록을 가져옴
        pageDiv.addEventListener('click', () => {
            if (containerName === 'vod-page-container') {
                loadVod(pageNumber + 1);
            }
            else if (containerName === 'live-page-container') {
                loadStandBy(pageNumber + 1, option);
            }
        });
        pageContainer.appendChild(pageDiv);
    }
}

function loadVodByCategory(categoryNo, pageNo) {
    axios.get(`/stream/vod/category`, {
        params: {
            categoryNo: categoryNo,
            pageNo: pageNo,
        }
    }).then(response => {
        const pageInfo = response.data['pageInfo'];
        const vodContent = pageInfo['content'];
        const vodGrid = document.getElementById('vod-grid');

        vodGrid.innerHTML = '';
        vodContent.forEach(vod => {
            const vodDiv = document.createElement('div');
            vodDiv.classList.add('item');
            const productPrice = vod.productPrice;
            const discountRate = vod.productSale;
            const streamStartTime = vod.streamStartTime;

            const discountedPrice = Math.floor(productPrice * ((100 - discountRate) / 100));

            const formattedOriginPrice = productPrice.toLocaleString('ko-KR');
            const formattedDiscountedPrice = discountedPrice.toLocaleString('ko-KR');

            const date = new Date(streamStartTime);

            // 년, 월, 일을 추출하여 포맷
            const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

            vodDiv.innerHTML = `
                    <div class="stream-img-container">
                        <img src="${vod.productImg}" alt="${vod.productName}" />
                    </div>
                    <p id="date">${formattedDate}</p>
                    <p id="title">${vod.streamTitle}</p>
                    <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedOriginPrice}원</p>
                    <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${vod.productSale} %</p>
                    <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedDiscountedPrice}원</p>
                `;

            if (discountRate === 0) {
                vodDiv.querySelector(".product-sale").style.display = "none";
                vodDiv.querySelector("#product-sale-percent").style.display = "none";
            } else {
                vodDiv.querySelector(".product-sale").style.display = "block";
                vodDiv.querySelector("#product-sale-percent").style.display = "block";
            }

            // VOD 클릭 시 상세 및 시청 페이지로 이동
            vodDiv.addEventListener('click', () => {
                window.location.href = `/watch/vod/${vod.streamNo}`;
            });

            vodGrid.appendChild(vodDiv);
        });
        renderPageContent(pageInfo, 'vod-page-container');
    })
        .catch(error => {
            console.error("VOD 목록을 불러오는 중 오류 발생:", error);
        });
}
