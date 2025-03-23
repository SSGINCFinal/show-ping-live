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

    window.history.pushState(null, "", window.location.href);
    window.addEventListener("popstate", preventBackNavigation);
});

// ✅ 로그인 함수 (관리자 계정일 경우 2FA 폼 표시)
async function login(event) {  // event 파라미터 추가
    event.preventDefault();

    const memberId = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await axios.post("/api/auth/login", {
            memberId: memberId,
            password: password
        }, {
            headers: { "Content-Type": "application/json" }
        });

        console.log("로그인 응답:", response.data);

        // 2FA가 필요한 경우
        if (response.data.status === "2FA_REQUIRED") {
            console.log("2FA 인증이 필요합니다! TOTP 입력창을 표시합니다.");
            sessionStorage.setItem("memberId", memberId); // 사용자 ID 저장 (TOTP 검증에 필요)
            fetchQrCode(memberId); // QR 코드 불러오기
            document.querySelector(".login-container").style.display = "none";
            document.getElementById("totp-form").style.display = "block";
        } else if (response.data.status === "LOGIN_SUCCESS") {
            console.log("로그인 성공!");
            // Access Token 저장
            if (response.data.accessToken) {
                sessionStorage.setItem("accessToken", response.data.accessToken);
                console.log("Access Token 저장 완료:", sessionStorage.getItem("accessToken"));
            }

            setTimeout(() => {
                window.location.href = "/";
            }, 500);
        } else{
            alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
        }
    } catch (error) {
        console.error("로그인 요청 실패:", error.response ? error.response.data : error);
        alert("로그인 실패! 아이디 또는 비밀번호를 확인하세요.");
    }
}
// ✅ TOTP (2단계 인증) 검증 함수
async function verifyTOTP(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = sessionStorage.getItem("memberId"); // 저장된 사용자 ID 가져오기
    const totpCode = document.getElementById("totpCode").value;

    if (!memberId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await axios.post("/api/admin/verify-totp", {
            adminId: memberId,
            totpCode: totpCode
        });

        console.log("🚀 TOTP 응답 데이터:", response.data);

        if (response.data.status === "LOGIN_SUCCESS") {
            console.log("2FA 인증 성공! 최종 로그인 완료");

            // 기존 Access Token을 다시 저장 (덮어쓰기 방지)
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
        alert("OTP 인증 실패! 다시 시도하세요.");
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
            Swal.fire({
                icon: 'error',
                title: 'QR 코드 오류',
                text: "QR 코드 불러오기 실패: " + response.data.message
            });
        }
    } catch (error) {
        console.error("QR 코드 로드 오류:", error);
        Swal.fire({
            icon: 'error',
            title: 'QR 코드 오류',
            text: 'QR 코드 로드 중 오류 발생!'
        });
    }
}
