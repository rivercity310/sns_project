const AJAX = {
    call: (url, params, func, isfd) => {
        const callobj = {
            url: url,
            type: "post",
            data: params,
            dataType: "text",
            success: func,
            error: (xhr, status, error) => {
                if (xhr.status == 0)
                    alert("네트워크 접속이 원활하지 않습니다.");
                else {
                    console.log(xhr.responseText);
                    alert("에러 발생!");
                }
            }
        };

        /*
        파일 업로드를 위해 multipart/form-data 형식으로 파라미터를 전송
        - processData : 주어진 파라미터가 이름-값 쌍의 리스트로 표현된 쿼리 스트링 형식으로 전달, default는 True
        - contentType : HTTP 요청의 인코딩 타입 설정, default는 True이며 이 경우 'application/www-form-urlencoded'로 설정
         */
        if (isfd) {
            callobj.processData = false;
            callobj.contentType = false;
        }

        jQuery.ajax(callobj);
    },
};

const Page = {
    init: function(cbfunc) {
        AJAX.call("view/session.jsp", null, function(data) {
            const uid = data.trim();
            if (uid == "null") {
                alert("로그인이 필요한 서비스입니다.");
                window.location.href = "login.html";
            }
            else {
                if (cbfunc != null)
                    cbfunc(uid);
            }
        });
    },
};
