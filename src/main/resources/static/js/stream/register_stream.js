document.addEventListener("DOMContentLoaded", function () {
    const updateBtn = document.querySelector(".update-btn");    // 업데이트 버튼

    updateBtn.addEventListener("click", function () {
        // streamNo는 전역으로 선언된 streamNo 사용

        // streamTitle : 사용자가 입력한 방송 제목
        const streamTitle = document.getElementById("broadcastTitle").value;

        // streamDescription : 사용자가 입력한 방송 설명
        const streamDescription = document.getElementById("broadcastDesc").value;

        // productNo : class가 product-info인 div 태그의 id 값
        const productDiv = document.querySelector(".product-element .product-info");
        const productNo = productDiv ? productDiv.id : null;

        // productSale : id가 discountRate인 input 태그의 value 값
        const productSale = document.getElementById("discountRate").value;

        if (!streamTitle || !productNo) {
            alert("방송 제목 입력과 상품 선택은 필수입니다.")
            return;
        }

        const data = {
            streamNo: streamNo,
            streamTitle: streamTitle,
            streamDescription: streamDescription,
            productNo: productNo,
            productSale: productSale
        };

        // axios로 방송을 등록
        axios.post("/stream/stream", data)
            .then((response) => {
                console.log("성공", response);
                streamNo = response.data.streamNo;

                $('#start').attr('disabled', false);
                $('#stop').attr('disabled', true);
            })
            .catch((error) => {
                console.log("실패", error);
            });
    });
});