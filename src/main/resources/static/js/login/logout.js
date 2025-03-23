document.addEventListener("DOMContentLoaded", function () {
    const accessToken = sessionStorage.getItem("accessToken");
    const authButton = document.getElementById("auth-button");
    const authIcon = document.getElementById("auth-icon");

    if (!authButton || !authIcon) return;

    if (accessToken) {
        // 로그인 상태 → 로그아웃 버튼으로 변경
        authButton.href = "#"; // 기본 링크 제거
        authIcon.src = "/img/icon/logout.png"; // 로그아웃 아이콘 표시
        authButton.addEventListener("click", function (event) {
            event.preventDefault(); // 기본 동작 방지
            logout(); // 로그아웃 함수 실행
        });
    } else {
        // 비로그인 상태 → 로그인 버튼으로 변경
        authButton.href = "/login"; // 로그인 페이지 이동
        authIcon.src = "/img/icon/login.png"; // 로그인 아이콘 표시
    }
});

function logout() {
    console.log("로그아웃 요청 실행");

    fetch("/api/auth/logout", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${sessionStorage.getItem("accessToken")}`,
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.ok) {
                sessionStorage.removeItem("accessToken");
                sessionStorage.removeItem("refreshToken");
                sessionStorage.removeItem("memberId");
                console.log("세션 삭제 완료, 메인 페이지로 이동");
                window.location.href = "/";
            } else {
                console.error("로그아웃 실패:", response.statusText);
            }
        })
        .catch(error => console.error("로그아웃 오류:", error));
}
