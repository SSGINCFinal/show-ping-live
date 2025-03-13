document.addEventListener("DOMContentLoaded", () => {
    // 페이지 로딩 시 신고 목록 호출
    loadReports();

    // 검색 폼 요소 선택
    const searchForm = document.getElementById("searchForm");
    const searchBtn = document.querySelector('.search-btn')
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
            searchForm.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
        });
    }

    // 기존 검색 버튼과 초기화 버튼 이벤트는 submit 이벤트에 위임할 수 있음
    if (resetBtn && searchForm) {
        resetBtn.addEventListener("click", () => {
            searchForm.reset();
            loadReports();
        });
    }



    // 초기화 버튼 클릭 시 폼 리셋 후 전체 신고 목록 호출
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
            }document.addEventListener("DOMContentLoaded", () => {
                // 페이지 로딩 시 신고 목록 호출
                loadReports();

                // 검색 폼 요소 선택
                const searchForm = document.getElementById("searchForm");
                const searchBtn = document.querySelector('.search-btn')
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
                        searchForm.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
                    });
                }

                // 기존 검색 버튼과 초기화 버튼 이벤트는 submit 이벤트에 위임할 수 있음
                if (resetBtn && searchForm) {
                    resetBtn.addEventListener("click", () => {
                        searchForm.reset();
                        loadReports();
                    });
                }



                // 초기화 버튼 클릭 시 폼 리셋 후 전체 신고 목록 호출
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


                        startDateInput.valueAsDate = start.toISOString().slice(0,10);
                        endDateInput.valueAsDate = end.toISOString().slice(0,10);
                        console.log("[DEBUG] startDate >> ", start);
                        console.log("[DEBUG] endDate >> ", end);
                    });
                });
            });

            /**
             * 신고 목록을 불러와 테이블을 동적으로 생성하는 함수
             * @param {string} [queryString=""] - 검색 조건 쿼리 스트링
             */
            function loadReports(queryString = "") {
                const searchForm = document.getElementById("searchForm");
                const formData = new FormData(searchForm);
                const params = Object.fromEntries(formData.entries());

                axios.get('/report/api/list', { params: params })
                    .then((response) => {
                        console.log("[DEBUG] Received report data:", response.data);
                        const reports = response.data;
                        const tableContainer = document.querySelector(".table-container");
                        tableContainer.innerHTML = ""; // 기존 내용 클리어

                        // 테이블 생성 (생성 로직은 이전과 동일)
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
                                const date = new Date(report.reportCreatedAt);
                                formattedDate = `${date.getFullYear()}-${("0" + (date.getMonth() + 1)).slice(-2)}-${("0" + date.getDate()).slice(-2)} ${("0" + date.getHours()).slice(-2)}:${("0" + date.getMinutes()).slice(-2)}`;
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

                        // 결과 개수 영역 업데이트 (예: <span> 태그 내부 업데이트)
                        const resultCountSpan = document.querySelector(".result-count span");
                        if (resultCountSpan) {
                            resultCountSpan.textContent = reports.length;
                        }
                    })
                    .catch((error) => {
                        console.error("신고 목록을 불러오는 중 오류 발생:", error);
                    });
            }


            startDateInput.valueAsDate = start;
            console.log("[DEBUG] startDate >> ", start);
            endDateInput.valueAsDate = end;
            console.log("[DEBUG] endDate >> ", end);
        });
    });
});

/**
 * 신고 목록을 불러와 테이블을 동적으로 생성하는 함수
 * @param {string} [queryString=""] - 검색 조건 쿼리 스트링
 */
function loadReports(queryString = "") {
    const searchForm = document.getElementById("searchForm");
    const formData = new FormData(searchForm);
    const params = Object.fromEntries(formData.entries());
    console.log("[DEBUG CHECK] params >> ", params);

    axios.get('/report/api/list', { params: params })
        .then((response) => {
            console.log("[DEBUG] Received report data:", response.data);
            const reports = response.data;
            const tableContainer = document.querySelector(".table-container");
            tableContainer.innerHTML = ""; // 기존 내용 클리어

            // 테이블 생성 (생성 로직은 이전과 동일)
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
                    // Date 객체로 변환 후 포맷팅 함수 사용
                    formattedDate = formatDate(new Date(report.reportCreatedAt));
                    console.log("[DEBUG] formattedDate >> ", formattedDate )
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

            // 결과 개수 영역 업데이트 (예: <span> 태그 내부 업데이트)
            const resultCountSpan = document.querySelector(".result-count span");
            if (resultCountSpan) {
                resultCountSpan.textContent = reports.length;
            }
        })
        .catch((error) => {
            console.error("신고 목록을 불러오는 중 오류 발생:", error);
        });
}

// 날짜 포맷팅 헬퍼 함수: YYYY-MM-DD HH:MM 형식으로 변환
function formatDate(dateObj) {
    const year = dateObj.getFullYear();
    const month = ("0" + (dateObj.getMonth() + 1)).slice(-2);
    const day = ("0" + dateObj.getDate()).slice(-2);
    const hours = ("0" + dateObj.getHours()).slice(-2);
    const minutes = ("0" + dateObj.getMinutes()).slice(-2);
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}