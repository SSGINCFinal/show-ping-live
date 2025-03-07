document.addEventListener("DOMContentLoaded", async function () {
    try {
        // 사용자 정보 가져오기
        const userResponse = await axios.get("/api/carts/info");
        const memberNo = userResponse.data.memberNo;

        if (!memberNo) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        // 최근 주문 정보 가져오기
        const orderResponse = await axios.get(`/api/orders/latest/${memberNo}`);
        const order = orderResponse.data;

        document.getElementById("order-no").textContent = order.ordersNo;
        document.getElementById("order-status").textContent = order.ordersStatus;
        document.getElementById("order-total").textContent = order.ordersTotalPrice.toLocaleString() + " 원";
        document.getElementById("order-date").textContent = new Date(order.ordersDate).toLocaleString();

        // 주문 상세 정보 가져오기
        const orderItemsList = document.getElementById("order-items");
        order.orderDetails.forEach(item => {
            const li = document.createElement("li");
            li.innerHTML = `${item.product.productName} x ${item.orderDetailQuantity} - <strong>${item.orderDetailTotalPrice.toLocaleString()} 원</strong>`;
            orderItemsList.appendChild(li);
        });

    } catch (error) {
        console.error("주문 정보 불러오기 실패:", error);
        alert("주문 내역을 불러오는 중 오류가 발생했습니다.");
    }
});