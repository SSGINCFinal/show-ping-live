document.addEventListener("DOMContentLoaded", function () {
    // ShowPing 로고 클릭 시 메인 페이지로 이동
    const showPingText = document.querySelector("h1");
    if (showPingText) {
        showPingText.style.cursor = "pointer";
        showPingText.addEventListener("click", function () {
            window.location.href = "/";
        });
    }

    // 2FA 입력폼에서 Enter 키를 눌렀을 때 인증 버튼 클릭
    const totpInput = document.getElementById("totpCode");
    if (totpInput) {
        totpInput.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                event.preventDefault(); // 기본 엔터 키 동작 방지
                verifyTOTP(event); // 인증 함수 호출
            }
        });
    }

    // ✅ 뒤로 가기 방지: 2FA 폼이 표시된 경우에만 작동
    function preventBackNavigation() {
        const totpForm = document.getElementById("totp-form");
        if (totpForm && totpForm.style.display !== "none") {
            alert("2차 인증이 완료될 때까지 이 페이지를 벗어날 수 없습니다.");
            history.go(1); // 뒤로 가기 방지
        }
    }

    window.history.pushState(null, "", window.location.href);
    window.addEventListener("popstate", preventBackNavigation);
});

// ✅ 로그인 함수 (관리자 계정일 경우 2FA 폼 표시)
async function login(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = document.getElementById("memberId").value.trim();
    const password = document.getElementById("memberPassword").value.trim();
    const loginContainer = document.querySelector(".login-container");
    const totpForm = document.getElementById("totp-form");

    if (!memberId || !password) {
        alert("아이디와 비밀번호를 입력해주세요.");
        return;
    }

    try {
        const response = await axios.post("/api/admin/login", {
            adminId: memberId,
            password: password
        }, {
            headers: { "Content-Type": "application/json" }
        });

        console.log("로그인 응답:", response.data);

        if (response.data.status === "2FA_REQUIRED") {
            console.log("2FA 인증이 필요합니다! TOTP 입력창을 표시합니다.");
            sessionStorage.setItem("accessToken", response.data.accessToken);
            sessionStorage.setItem("memberId", memberId);
            fetchQrCode(memberId);

            // ✅ 로그인 폼 숨기고 2FA 폼 표시
            if (loginContainer) loginContainer.style.display = "none";
            if (totpForm) totpForm.style.display = "block";

        } else if (response.data.status === "LOGIN_SUCCESS") {
            console.log("로그인 성공!");
            if (response.data.accessToken) {
                sessionStorage.setItem("accessToken", response.data.accessToken);
                console.log("Access Token 저장 완료:", sessionStorage.getItem("accessToken"));
            }

            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } else {
            alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
            if (loginContainer) loginContainer.style.display = "block";
            if (totpForm) totpForm.style.display = "none"; // ✅ 로그인 실패 시 2FA 폼 숨김
        }
    } catch (error) {
        console.error("로그인 요청 실패:", error.response ? error.response.data : error);
        alert(error.response?.data?.message || "서버 오류 발생! 다시 시도해주세요.");
        if (loginContainer) loginContainer.style.display = "block";
        if (totpForm) totpForm.style.display = "none"; // ✅ 오류 발생 시 2FA 폼 숨김
    }
}

// ✅ TOTP (2단계 인증) 검증 함수
async function verifyTOTP(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = sessionStorage.getItem("memberId");
    const totpCode = document.getElementById("totpCode").value.trim();
    const accessToken = sessionStorage.getItem("accessToken");

    if (!memberId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return;
    }

    if (!totpCode) {
        alert("OTP 코드를 입력하세요.");
        return;
    }

    try {
        const response = await axios.post("/api/admin/verify-totp", {
            adminId: memberId,
            totpCode: totpCode,
            accessToken: accessToken
        });

        console.log("🚀 TOTP 응답 데이터:", response.data);

        if (response.data.status === "LOGIN_SUCCESS") {
            console.log("2FA 인증 성공! 최종 로그인 완료");

            if (response.data.accessToken) {
                sessionStorage.setItem("accessToken", response.data.accessToken);
                console.log("최종 Access Token 저장 완료:", sessionStorage.getItem("accessToken"));
            }

            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } else {
            alert("OTP 인증 실패! 다시 시도하세요.");
        }
    } catch (error) {
        console.error("TOTP 인증 실패:", error.response ? error.response.data : error);
        alert("인증번호를 다시 확인해주세요.");
    }
}

// ✅ QR 코드 가져오는 함수 (2FA 활성화 시 사용)
async function fetchQrCode(adminId) {
    try {
        const response = await axios.get(`/api/admin/totp-setup/${adminId}`);
        if (response.data.status === "SUCCESS") {
            const qrCodeUrl = response.data.qrCodeUrl;

            // ✅ QR 코드 이미지 업데이트
            document.getElementById("qrCodeImage").src =
                `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(qrCodeUrl)}`;
            document.getElementById("totp-form").style.display = "block"; // QR 코드 폼 표시
        } else {
            alert("QR 코드 불러오기 실패: " + response.data.message);
        }
    } catch (error) {
        console.error("QR 코드 로드 오류:", error);
        alert("QR 코드 로드 중 오류 발생!");
    }
}
