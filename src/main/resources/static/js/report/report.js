// [추가!!!!] 페이지네이션 관련 전역 변수
let allReports = [];     // 전체 신고 목록
let currentPage = 1;     // 현재 페이지
const itemsPerPage = 20; // 페이지당 표시할 신고 건수

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

    // 검색 버튼 클릭 시, 폼 밖에 있어도 수동으로 폼의 submit 이벤트를 발생시킵니다.
    if (searchBtn && searchForm) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            // 폼의 submit 이벤트를 강제로 발생시킵니다.
            searchForm.dispatchEvent(new Event("submit", {bubbles: true, cancelable: true}));
        });
    }

    // 기존 검색 버튼과 초기화 버튼 이벤트는 submit 이벤트에 위임할 수 있음
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
            // [변경!!!!] valueAsDate 대신 "YYYY-MM-DD" 문자열로 직접 설정
            startDateInput.value = start.toISOString().slice(0, 10);
            endDateInput.value = end.toISOString().slice(0, 10);
        });
    });
});

/**
 * [변경!!!!] 신고 목록을 서버에서 받아온 뒤, 페이지/테이블을 렌더링
 */
function loadReports() {
    const searchForm = document.getElementById("searchForm");
    const formData = new FormData(searchForm);
    const params = Object.fromEntries(formData.entries());

    axios.get('/report/api/list', {params: params})
        .then((response) => {
            console.log("[DEBUG] Received report data:", response.data);
            // [추가!!!!] 전체 신고 목록을 전역 변수에 저장하고, 페이지를 1로 초기화
            allReports = response.data;
            currentPage = 1;
            renderPage();
        })
        .catch((error) => {
            console.error("신고 목록을 불러오는 중 오류 발생:", error);
        });
}

/**
 * [추가!!!!] 현재 페이지의 데이터만 테이블에 표시하고, 페이지네이션 표시
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
 * [추가!!!!] 테이블 생성 로직 (기존 loadReports 내부의 테이블 생성 부분을 분리)
 */
function renderTable(reports) {
    const tableContainer = document.querySelector(".table-container");
    tableContainer.innerHTML = ""; // 기존 내용 클리어

    const table = document.createElement("table");
    const thead = document.createElement("thead");
    thead.innerHTML = `
        <tr>
            <th>번호</th>
            <th>접수일</th>
            <th>신고 사유</th>
            <th>피신고자 ID</th>
            <th>처리 여부</th>
        </tr>
    `;
    table.appendChild(thead);

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
 * [추가!!!!] 페이지네이션 렌더링
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
 * [추가!!!!] 날짜 포맷팅 함수 (YYYY-MM-DD HH:MM)
 */
function formatDate(dateObj) {
    const year = dateObj.getFullYear();
    const month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
    const day = ("0" + dateObj.getDate()).slice(-2);
    const hours = ("0" + dateObj.getHours()).slice(-2);
    const minutes = ("0" + dateObj.getMinutes()).slice(-2);
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}
