let allReports = [];     // 전체 신고 목록
let currentPage = 1;     // 현재 페이지
const itemsPerPage = 20; // 페이지당 표시할 신고 건수
let sortState = {};

document.addEventListener("DOMContentLoaded", () => {
    // 페이지 로딩 시 신고 목록 호출
    loadReports();

    // 검색 폼 요소 선택
    const searchForm = document.getElementById("searchForm");
    const searchBtn = document.querySelector('.search-btn');
    const resetBtn = document.querySelector(".reset-btn");

    // 모든 검색 이벤트를 submit 이벤트에서 처리
    if (searchForm) {
        searchForm.addEventListener("submit", (e) => {
            e.preventDefault();  // 기본 제출 방지
            loadReports();       // AJAX로 결과 업데이트
        });
    }

    // 검색 버튼 클릭 시, 폼 밖에 있어도 수동으로 폼의 submit 이벤트 발생.
    if (searchBtn && searchForm) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            // 폼의 submit 이벤트를 강제로 발생시킵니다.
            searchForm.dispatchEvent(new Event("submit", {bubbles: true, cancelable: true}));
        });
    }

    if (resetBtn && searchForm) {
        resetBtn.addEventListener("click", () => {
            searchForm.reset();
            loadReports();
        });
    }

    // 날짜 범위 버튼 처리
    const dateBtns = document.querySelectorAll(".date-btn");
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    dateBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
            const range = btn.dataset.range;
            const today = new Date();
            let start, end;

            switch (range) {
                case "today":
                    start = new Date();
                    end = new Date();
                    break;
                case "week":
                    start = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);
                    end = today;
                    break;
                case "month":
                    start = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
                    end = today;
                    break;
                case "all":
                    startDateInput.value = "";
                    endDateInput.value = "";
                    return;
                default:
                    return;
            }
            // "YYYY-MM-DD" 문자열 직접 설정
            startDateInput.value = start.toISOString().slice(0, 10);
            endDateInput.value = end.toISOString().slice(0, 10);
        });
    });
});

/**
 * 신고 목록을 서버에서 받아온 뒤, 페이지/테이블을 렌더링
 */
function loadReports() {
    const searchForm = document.getElementById("searchForm");
    const formData = new FormData(searchForm);
    const params = Object.fromEntries(formData.entries());

    axios.get('/report/api/list', {params: params})
        .then((response) => {
            console.log("[DEBUG] Received report data:", response.data);
            // 전체 신고 목록을 전역 변수에 저장하고, 페이지를 1로 초기화
            allReports = response.data;
            currentPage = 1;
            renderPage();
        })
        .catch((error) => {
            console.error("신고 목록을 불러오는 중 오류 발생:", error);
        });
}

/**
 * 현재 페이지의 데이터만 테이블에 표시하고, 페이지네이션 표시
 */
function renderPage() {
    // 현재 페이지의 데이터 슬라이싱
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = allReports.slice(startIndex, endIndex);

    // 테이블 렌더링
    renderTable(pageData);

    // 페이지네이션 렌더링
    renderPagination();
}

/**
 * 테이블 생성 로직
 */
function renderTable(reports) {
    const tableContainer = document.querySelector(".table-container");
    tableContainer.innerHTML = ""; // 기존 내용 클리어

    const headerColumns = [
        {title: "번호", field: "reportNo", type: "number"},
        {title: "접수일", field: "reportCreatedAt", type: "date"},
        {title: "신고 사유", field: "reportReason", type: "string"},
        {title: "피신고자 ID", field: "memberId", type: "string"},
        {title: "처리 여부", field: "reportStatus", type: "string"}
    ];

    const table = document.createElement("table");
    const thead = document.createElement("thead");

    let headerHtml = "<tr>";
    headerColumns.forEach(col => {
        headerHtml += `<th data-field="${col.field}" data-type="${col.type}" style="cursor:pointer;">
            ${col.title} <span class="sort-arrow">&#x21C5;</span>
        </th>`;
    });
    headerHtml += "</tr>";
    thead.innerHTML = headerHtml;

    table.appendChild(thead);

    // 헤더 클릭 이벤트 설정 (정렬 기능)
    const thElements = thead.querySelectorAll("th");
    thElements.forEach(th => {
        th.addEventListener("click", () => {
            const field = th.getAttribute("data-field");
            const type = th.getAttribute("data-type");
            sortReports(field, type);
        });
    });

    const tbody = document.createElement("tbody");
    reports.forEach((report) => {
        const tr = document.createElement("tr");

        let formattedDate = "";
        if (report.reportCreatedAt) {
            formattedDate = formatDate(new Date(report.reportCreatedAt));
        }

        tr.innerHTML = `
            <td>${report.reportNo}</td>
            <td>${formattedDate}</td>
            <td>${report.reportReason}</td>
            <td>${report.memberId}</td>
            <td>${report.reportStatus}</td>
        `;
        tr.addEventListener("click", () => {
            window.location.href = `/report/detail/${report.reportNo}`;
        });
        tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    tableContainer.appendChild(table);

    // 결과 개수 영역 업데이트
    const resultCountSpan = document.querySelector(".result-count span");
    if (resultCountSpan) {
        resultCountSpan.textContent = allReports.length;
    }
}

/**
 *   페이지네이션 렌더링
 * - 한 페이지당 itemsPerPage(20건)
 * - ◀, ▶ 버튼 누르면 10페이지씩 이동
 */
function renderPagination() {
    const paginationContainer = document.querySelector(".pagination-container");
    paginationContainer.innerHTML = ""; // 초기화

    const totalItems = allReports.length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);

    // 페이지가 1개 이하이면 페이지네이션 표시 X
    if (totalPages <= 1) return;

    // ul 생성
    const ul = document.createElement("ul");
    ul.classList.add("pagination");

    // 10페이지씩 묶어서 표시
    const pageGroup = Math.floor((currentPage - 1) / 10);
    const startPage = pageGroup * 10 + 1;
    let endPage = startPage + 9;
    if (endPage > totalPages) {
        endPage = totalPages;
    }

    // ◀ 이전 그룹
    if (startPage > 10) {
        const liPrev = document.createElement("li");
        liPrev.innerHTML = `<a href="javascript:void(0)">◀</a>`;
        liPrev.addEventListener("click", () => {
            currentPage = startPage - 1;
            renderPage();
        });
        ul.appendChild(liPrev);
    }

    // 페이지 번호
    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement("li");
        li.innerHTML = `<a href="javascript:void(0)">${i}</a>`;
        if (i === currentPage) {
            li.classList.add("active");
        }
        li.addEventListener("click", () => {
            currentPage = i;
            renderPage();
        });
        ul.appendChild(li);
    }

    // ▶ 다음 그룹
    if (endPage < totalPages) {
        const liNext = document.createElement("li");
        liNext.innerHTML = `<a href="javascript:void(0)">▶</a>`;
        liNext.addEventListener("click", () => {
            currentPage = endPage + 1;
            renderPage();
        });
        ul.appendChild(liNext);
    }

    paginationContainer.appendChild(ul);
}

/**
 *  날짜 포맷팅 함수 (YYYY-MM-DD HH:MM)
 */
function formatDate(dateObj) {
    const year = dateObj.getFullYear();
    const month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
    const day = ("0" + dateObj.getDate()).slice(-2);
    const hours = ("0" + dateObj.getHours()).slice(-2);
    const minutes = ("0" + dateObj.getMinutes()).slice(-2);
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 정렬 함수: 주어진 필드와 타입에 따라 allReports를 정렬하고 renderPage() 호출
function sortReports(field, type) {
    // 기본 정렬 순서를 asc로 설정
    if (!sortState[field] || sortState[field] === 'desc') {
        sortState[field] = 'asc';
    } else {
        sortState[field] = 'desc';
    }

    // 정렬 비교 함수 생성
    const compare = (a, b) => {
        let aValue = a[field];
        let bValue = b[field];

        // 타입별 변환
        if (type === 'number') {
            aValue = Number(aValue);
            bValue = Number(bValue);
        } else if (type === 'date') {
            aValue = new Date(aValue);
            bValue = new Date(bValue);
        } else {
            // 문자열 비교: 대소문자 구분 없이
            aValue = aValue.toString().toLowerCase();
            bValue = bValue.toString().toLowerCase();
        }

        if (aValue < bValue) return sortState[field] === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortState[field] === 'asc' ? 1 : -1;
        return 0;
    };

    // 정렬 적용
    allReports.sort(compare);
    // 정렬 후 현재 페이지 다시 렌더링
    renderPage();
}