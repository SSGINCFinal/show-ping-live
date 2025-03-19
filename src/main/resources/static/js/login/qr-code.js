async function fetchQrCode() {
    const adminId = sessionStorage.getItem("memberId"); // 로그인된 사용자 ID 가져오기

    if (!adminId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        window.location.href = "/login"; // 로그인 페이지로 이동
        return;
    }

    try {
        const response = await axios.get(`/api/admin/totp-setup/${adminId}`);
        if (response.data.status === "SUCCESS") {
            document.getElementById("qrCodeImage").src =
                `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(response.data.qrCodeUrl)}`;
        } else {
            alert("QR 코드 불러오기 실패: " + response.data.message);
        }
    } catch (error) {
        console.error("QR 코드 로드 오류:", error);
        alert("QR 코드 로드 중 오류 발생!");
    }
}

// 페이지 로드 시 QR 코드 가져오기
window.onload = fetchQrCode;