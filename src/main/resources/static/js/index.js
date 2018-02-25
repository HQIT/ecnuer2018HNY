var app = null;
var config = {
    el: '#app',
    data: {
        user: {},
        message: {}
    },
    methods: {
        send: function () {
            window.location.href = baseUrl + '/message/editor';
            return false;
        }
    }
};

var start = function () {
    Vue.http.get(baseUrl + '/api/user').then(function (res) {
        _USER = res.data;
        if (!_USER) {
            window.location.href = [baseUrl + '/wx/login', '?back=/message'].join('');
            return false;
        } else {
            window.location.href = baseUrl + '/message';
        }
    }, function (e) {
        console.error(e);
    });
};

var getJsapiSignature = function () {
    Vue.http.post(baseUrl + '/wx/signature', {
        url: window.location.href
    }).then(function (res) {
        if (res.status === 200) {
            var data = res.data;
            wx.config({
                debug: false,
                appId: data.appId,
                timestamp: data.timestamp,
                nonceStr: data.nonceStr,
                signature: data.signature,
                jsApiList: [
                    'hideAllNonBaseMenuItem'
                ]
            });
        }
    }, function (e) {
        console.log(e);
    });
};
var timestamp = Date.now() + 3000;
wx.ready(function () {
    app = new Vue(config);
    wx.hideAllNonBaseMenuItem();
    if (timestamp <= Date.now()) start()
    else {
        window.setTimeout(start, timestamp - Date.now());
    }
});

// 第一步
getJsapiSignature();