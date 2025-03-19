let currentPage = 0;
const itemsPerPage = 8;
const categoryNo = window.location.pathname.split('/').pop();
let sortOption = "quantity-desc"; // 기본 정렬 기준

document.addEventListener("DOMContentLoaded", function () {
    loadProducts();
    setupSortButtons();
});

document.getElementById('load-more').addEventListener('click', loadProducts);

// 정렬 버튼 클릭 시
function setupSortButtons() {
    const buttons = document.querySelectorAll('.sort-btn');
    buttons.forEach(button => {
        button.addEventListener('click', function () {
            // 클릭된 버튼에 'selected' 클래스를 추가하고 나머지 버튼에서 제거
            buttons.forEach(btn => btn.classList.remove('selected'));
            button.classList.add('selected');

            // 정렬 기준 업데이트
            sortOption = button.id.replace('sort-', ''); // 예: 'price-asc', 'price-desc' 등
            currentPage = 0;
            document.getElementById('product-grid').innerHTML = ''; // 기존 상품 리스트 비우기
            loadProducts();
        });
    });
}

function loadProducts() {
    axios.get(`/api/products/${categoryNo}?page=${currentPage}&size=${itemsPerPage}&sort=${sortOption}`)
        .then(response => {
            const products = response.data.content;
            renderProducts(products);
            currentPage++;

            if (response.data.last) {
                document.getElementById('load-more').style.display = 'none';
            } else {
                document.getElementById('load-more').style.display = 'block';
            }
        })
        .catch(error => {
            console.error("상품 목록을 불러오는 중 오류 발생:", error);
        });
}

function renderProducts(products) {
    const productGrid = document.getElementById('product-grid');

    products.forEach(product => {
        const productDiv = document.createElement('div');
        productDiv.classList.add('product-item');
        const formattedPrice = product.productPrice.toLocaleString('ko-KR');
        const formattedFinalPrice = product.discountedPrice.toLocaleString('ko-KR');
        const productSale = product.productSale;

        let productName = product.productName;
        if (productName.length > 28) {
            productName = productName.substring(0, 28) + '...';
        }

        productDiv.innerHTML = `
            <div class="product-img-container">
                <img id="product-sale-icon" src="/img/icon/sale.png" alt="product-sale" class="sale-icon" style="width: 50px" />
                <img src="${product.productImg}" alt="${productName}" class="product-img" />
            </div>
            <p class="product-name">${productName}</p>
            <p class="product-sale" id="product-sale" style="text-decoration: line-through; font-size: 15px">${formattedPrice}원</p>
            <p class="product-sale-percent" id="product-sale-percent" style="color: red; font-size: 15px">${product.productSale} %</p>
            <p class="product-price-final" id="product-price-final" style="font-size: 20px">${formattedFinalPrice}원</p>
        `;

        if (productSale === 0) {
            productDiv.querySelector(".product-sale").style.display = "none";
            productDiv.querySelector("#product-sale-icon").style.display = "none";
            productDiv.querySelector("#product-sale-percent").style.display = "none";
        } else {
            productDiv.querySelector(".product-sale").style.display = "block";
            productDiv.querySelector("#product-sale-icon").style.display = "block";
            productDiv.querySelector("#product-sale-percent").style.display = "block";
        }

        productDiv.addEventListener('click', () => {
            window.location.href = `/product/detail/${product.productNo}`;
        });

        productGrid.appendChild(productDiv);
    });
}
