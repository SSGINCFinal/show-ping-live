document.addEventListener("DOMContentLoaded", function () {
    loadLive();                 // 단일 라이브 방송 불러오기
    loadVod(0);        // 최초 1페이지의 VOD 목록 불러오기
});

function loadLive() {
    axios.get('/stream/live')
        .then(response => {
            console.log(response.data);
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
                const streamStartTime = live.streamStartTime;

                const discountedPrice = Math.floor(productPrice * ((100 - discountRate) / 100));

                const formattedOriginPrice = productPrice.toLocaleString('ko-KR');
                const formattedDiscountedPrice = discountedPrice.toLocaleString('ko-KR');

                const date = new Date(streamStartTime);

                // 년, 월, 일을 추출하여 포맷
                const formattedDate = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;

                liveDiv.innerHTML = `
                    <div class="product-img-container">
                        <img id="product-sale-icon" src="/img/icon/sale.png" alt="product-sale" class="sale-icon" style="width: 50px" />
                        <img src="${live.productImg}" alt="${live.productName}" />
                    </div>
                    <p id="date">${formattedDate}</p>
                    <p id="title">${live.streamTitle}</p>
                    <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedOriginPrice}원</p>
                    <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${live.productSale} %</p>
                    <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedDiscountedPrice}원</p>
                `;

                // 라이브 클릭 시 시청 및 상세 페이지로 이동
                liveDiv.addEventListener('click', () => {
                    window.location.href = `/webrtc/watch/${live.streamNo}`;
                });
                liveGrid.appendChild(liveDiv);
            }
        })
        .catch(error => {
            console.error("라이브 목록을 불러오는 중 오류 발생:", error);
        });
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
                    <div class="product-img-container">
                        <img id="product-sale-icon" src="/img/icon/sale.png" alt="product-sale" class="sale-icon" style="width: 50px" />
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
                    vodDiv.querySelector("#product-sale-icon").style.display = "none";
                    vodDiv.querySelector("#product-sale-percent").style.display = "none";
                } else {
                    vodDiv.querySelector(".product-sale").style.display = "block";
                    vodDiv.querySelector("#product-sale-icon").style.display = "block";
                    vodDiv.querySelector("#product-sale-percent").style.display = "block";
                }

                // VOD 클릭 시 상세 및 시청 페이지로 이동
                vodDiv.addEventListener('click', () => {
                    window.location.href = `/watch/vod/${vod.streamNo}`;
                });

                vodGrid.appendChild(vodDiv);
            });

            // 페이지 버튼 영역 생성
            const pageContainer = document.getElementById('page-container');
            pageContainer.innerHTML = '';
            const totalPages = pageInfo['totalPages'];
            const pageNumber = pageInfo['number'];

            if (pageNumber !== totalPages - 1) {
                const pageDiv = document.createElement('div');
                pageDiv.classList.add('page-item');
                pageDiv.innerHTML = `<button class="load-more">더보기</button>`

                // 페이지 번호 클릭시 해당 페이지의 VOD 목록을 가져옴
                pageDiv.addEventListener('click', () => {
                    loadVod(pageNumber + 1);
                });
                pageContainer.appendChild(pageDiv);
            }
        })
        .catch(error => {
            console.error("VOD 목록을 불러오는 중 오류 발생:", error);
        });
}