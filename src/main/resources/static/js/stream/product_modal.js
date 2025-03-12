// 페이지가 로드된 후 실행
document.addEventListener("DOMContentLoaded", function() {
    const selectBtn = document.querySelector(".select-btn");    // + 선택 버튼
    const productModal = document.getElementById("productModal");   // 모달 영역
    const closeBtn = document.querySelector(".close");  // 닫기(X) 버튼
    const productItems = document.querySelectorAll(".product-item");    // 상품 목록
    const productElement = document.querySelector(".product-element");  // 선택된 상품 표시 영역

    // 1) + 선택 버튼을 누르면 모달 열기
    selectBtn.addEventListener("click", function() {
        productModal.style.display = "block";
    });

    // 2) 닫기 버튼(X)을 누르면 모달 닫기
    closeBtn.addEventListener("click", function() {
        productModal.style.display = "none";
    });

    // 3) 모달 영역 바깥을 클릭하면 모달 닫기
    window.addEventListener("click", function(e) {
        if (e.target === productModal) {
            productModal.style.display = "none";
        }
    });

    // 4) 각 상품 클릭 시, 선택된 상품 정보 표시 후 모달 닫기
    productItems.forEach((item) => {
        item.addEventListener("click", () => {
            // 선택된 상품의 정보 추출
            const imgSrc = item.querySelector("img").src;
            const name = item.querySelector(".product-name").textContent.trim();
            const originalPrice = item.querySelector(".original-price").textContent.trim();

            // 선택된 상품 표시 영역 내부를 동적으로 구성
            productElement.innerHTML = `
                <img src="${imgSrc}" alt="상품 이미지" class="product-img">
                <div class="product-info">
                    <p class="product-name">${name}</p>
                    <div class="product-price-container">
                        <p class="product-origin-price">${originalPrice}</p>
                    </div>
                </div>
            `;

            productModal.style.display = "none";
        });
    })
});
