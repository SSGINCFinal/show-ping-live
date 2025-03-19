let memberNo = null;
let orders = []; // 주문 목록을 전역 변수로 저장

document.addEventListener("DOMContentLoaded", async function () {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
        window.location.href = "/login";
        return;
    }

    try {
        // 로그인 사용자 정보
        const response = await axios.get("/api/carts/info", {
            headers: { Authorization: `Bearer ${token}` }
        });
        memberNo = response.data.memberNo;

        if (!memberNo) {
            alert("사용자 정보가 없습니다.");
            return;
        }

        // 회원의 전체 주문 목록 가져오기
        const orderResponse = await axios.get(`/api/orders/member/${memberNo}`);
        orders = orderResponse.data;  // 전역 변수에 저장

        // 처음 로딩 시, 전체 주문 목록 그리기
        renderOrders(orders);

        // "검색" 버튼 클릭 이벤트
        const searchButton = document.getElementById("search-button");
        searchButton.addEventListener("click", function () {
            const startDateVal = document.getElementById("start-date").value; // "YYYY-MM-DD" 형식
            const endDateVal   = document.getElementById("end-date").value;   // "YYYY-MM-DD" 형식

            // 날짜 값이 둘 다 입력되지 않은 경우 전체 목록 다시 표시
            if (!startDateVal && !endDateVal) {
                renderOrders(orders);
                return;
            }

            // 날짜 필터링
            const filtered = orders.filter(order => {
                const orderDate = new Date(order.ordersDate); // 주문 일시
                // orderDate를 비교하기 위해 startDate, endDate도 Date 객체로 변환
                const startDate = startDateVal ? new Date(startDateVal) : null;
                const endDate = endDateVal ? new Date(endDateVal) : null;

                // 주문 날짜가 startDate 이후이고 endDate 이전(또는 같은)인지 체크
                // startDate가 없으면 무조건 통과, endDate가 없으면 무조건 통과
                if (startDate && orderDate < startDate) return false;
                if (endDate && orderDate > endDate) return false;
                return true;
            });

            // 필터링된 목록을 다시 렌더링
            renderOrders(filtered);
        });

    } catch (error) {
        console.error("주문 정보 불러오기 실패:", error);
        document.getElementById("order-list").innerHTML = "<p>주문 정보를 불러오는 중 오류가 발생했습니다.</p>";
    }
});

/**
 * 주문 목록을 받아 화면에 그려주는 함수
 * (기존 코드에서 order 목록을 그리던 로직을 함수로 분리)
 */
function renderOrders(orderList) {
    const orderContainer = document.getElementById("order-list");
    orderContainer.innerHTML = ""; // 기존 내용 초기화

    if (!orderList || orderList.length === 0) {
        orderContainer.innerHTML = "<p>최근 주문 내역이 없습니다.</p>";
        return;
    }

    orderList.forEach(order => {
        let orderStatusText = "";
        switch (order.ordersStatus) {
            case "READY":
                orderStatusText = "상품 준비 중";
                break;
            case "TRANSIT":
                orderStatusText = "배송 중";
                break;
            case "COMPLETE":
                orderStatusText = "배송 완료";
                break;
            default:
                orderStatusText = order.ordersStatus;
        }

        const orderDiv = document.createElement("div");
        orderDiv.classList.add("order-box");
        orderDiv.innerHTML = `
            <h3>주문 번호: ${order.ordersNo}</h3>
            <p><strong>주문 상태:</strong> ${orderStatusText}</p>
            <p><strong>총 결제 금액:</strong> ${order.ordersTotalPrice.toLocaleString()} 원</p>
            <p><strong>주문 일시:</strong> ${new Date(order.ordersDate).toLocaleString()}</p>
            <button class="toggle-details" data-order-no="${order.ordersNo}">상세 보기</button>
            <div class="order-details" id="order-details-${order.ordersNo}" style="display: none;">
                <ul id="order-items-${order.ordersNo}"></ul>
            </div>
        `;
        orderContainer.appendChild(orderDiv);
    });

    // 상세보기 버튼 이벤트 (기존 코드 재활용)
    document.querySelectorAll(".toggle-details").forEach(button => {
        button.addEventListener("click", async function () {
            const orderNo = this.getAttribute("data-order-no");
            const detailsDiv = document.getElementById(`order-details-${orderNo}`);

            // 이미 열려 있다면 닫기
            if (detailsDiv.style.display === "block") {
                detailsDiv.style.display = "none";
                this.textContent = "상세 보기";
                return;
            }

            // 숨겨져 있으면 상세 내역 요청
            try {
                const orderDetailResponse = await axios.get(`/api/orders/${orderNo}/details`);
                const orderDetails = orderDetailResponse.data;

                const orderItemsList = document.getElementById(`order-items-${orderNo}`);
                orderItemsList.innerHTML = ""; // 기존 리스트 초기화

                orderDetails.forEach(item => {
                    const li = document.createElement("li");
                    li.innerHTML = `상품 이름: ${item.productName} | 수량: ${item.orderDetailQuantity} | 가격: <strong>${item.orderDetailTotalPrice.toLocaleString()} 원</strong>`;
                    orderItemsList.appendChild(li);
                });

                detailsDiv.style.display = "block";
                this.textContent = "상세 보기 닫기";
            } catch (error) {
                console.error(`주문 ${orderNo}의 상세 정보를 불러오는 중 오류 발생:`, error);
            }
        });
    });
}

document.querySelectorAll(".year-btn").forEach(button => {
    button.addEventListener("click", function() {
        const year = parseInt(this.getAttribute("data-year"));

        // 클라이언트 필터링 방식
        const filtered = orders.filter(order => {
            const orderYear = new Date(order.ordersDate).getFullYear();
            return orderYear === year;
        });
        renderOrders(filtered);

        // 또는 서버 API를 호출해 해당 연도 주문만 가져오는 방식
        // const orderResponse = await axios.get(`/api/orders/member/${memberNo}?year=${year}`);
        // renderOrders(orderResponse.data);
    });
});
