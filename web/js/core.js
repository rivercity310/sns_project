const AJAX = {
    call: (url, params, func) => {
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
        }

        jQuery.ajax(callobj);
    }
}
