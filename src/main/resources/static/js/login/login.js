window.onload = function () {
    const messageElement = document.getElementById("login-message");
    if (messageElement) {
        const message = messageElement.dataset.message || ""; // undefined 방지
        if (message.trim() !== '') {
            alert(message);
        }
    }

    // 2FA 입력폼에서 엔터 키를 눌렀을 때 인증 버튼 클릭하도록 처리
    const totpInput = document.getElementById("totpCode");
    if (totpInput) {
        totpInput.addEventListener("keypress", function(event) {
            if (event.key === "Enter") {
                event.preventDefault(); // 기본 엔터 키 동작 방지 (폼 제출 방지)
                verifyTOTP(event); // 인증 함수 호출
            }
        });
    }
};

function kakaoLogin() {
    // 카카오 로그인 페이지로 리다이렉트
    // (백엔드에서 카카오 인증 URL로 다시 리다이렉트 시키거나,
    //  혹은 이미 세팅된 소셜 로그인 로직으로 바로 연결)
    window.location.href = "/oauth/kakao";
}

function naverLogin() {
    window.location.href = "/oauth/naver";
}

// QR 코드 가져오는 함수 추가 (에러 해결)
async function fetchQrCode(adminId) {
    try {
        const response = await axios.get(`/api/admin/totp-setup/${adminId}`);
        if (response.data.status === "SUCCESS") {
            const qrCodeUrl = response.data.qrCodeUrl;

            // ✅ QR 코드 이미지 업데이트
            document.getElementById("qrCodeImage").src = `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(qrCodeUrl)}`;
            document.getElementById("totp-form").style.display = "block"; // QR 코드 폼 표시
        } else {
            alert("QR 코드 불러오기 실패: " + response.data.message);
        }
    } catch (error) {
        console.error("QR 코드 로드 오류:", error);
        alert("QR 코드 로드 중 오류 발생!")
    }
}

async function login(event) {  // event 파라미터 추가
    event.preventDefault();

    const memberId = document.getElementById("memberId").value;
    const password = document.getElementById("memberPassword").value;

    try {
        const response = await axios.post("/api/admin/login", {
            adminId: memberId,
            password: password
        }, {
            headers: { "Content-Type": "application/json" }
        });

        console.log("로그인 응답:", response.data);

        // 2FA가 필요한 경우
        if (response.data.status === "2FA_REQUIRED") {
            console.log("2FA 인증이 필요합니다! TOTP 입력창을 표시합니다.");
            sessionStorage.setItem("accessToken", response.data.accessToken);
            sessionStorage.setItem("memberId", memberId); // 사용자 ID 저장 (TOTP 검증에 필요)
            fetchQrCode(memberId); // QR 코드 불러오기
            document.querySelector(".login-form").style.display = "none";
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

async function verifyTOTP(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const memberId = sessionStorage.getItem("memberId"); // 저장된 사용자 ID 가져오기
    const totpCode = document.getElementById("totpCode").value;
    const accessToken = sessionStorage.getItem("accessToken"); // 기존 Access Token 유지

    if (!memberId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await axios.post("/api/admin/verify-totp", {
            adminId: memberId,
            totpCode: totpCode,
            accessToken: accessToken // 기존 Access Token 전달
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