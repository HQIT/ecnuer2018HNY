const MAX_MSG_LEN = 120;
var app = new Vue({
    el: '#app',
    data: {
        user: {},
        showError: false,
        errorMessage: '',
        message: {
            author: '',
            content: ''
        }
    },
    computed: {
        default_message: function () {
            /// TODO: 随机
            return '祝ECNU校友新年快乐, 狗年大吉!';
        },
        message_size: function () {
            return this.message.content ?
                this.message.content.length :
                0;
        },
        message_size_max: function () {
            return MAX_MSG_LEN;
        },
        message_content: {
            get: function () {
                if (!this.message.content)
                    this.message.content = this.default_message;
                return this.message.content;
            },
            set: function (val) {
                this.message.content = val;
                if (val.length > this.message_size_max) {
                    this.message.content = val.substr(0, this.message_size_max);
                    weui.topTips('120字就够啦', 1000);
                }
            }
        }
    },
    methods: {
        modifyAuthor: function () {
          this.$refs.author.focus();
        },
        commit: function () {
            var vm = this;
            var msg = this.message;
            vm.showError = false;
            msg.sender = {
                openid: this.user.openId,
                name: this.user.realname || this.user.nickname,
                portrait: this.user.portrait
            };
            if (!msg.author.length) {
                msg.author = this.user.realname || this.user.nickname;
            }
            if (!msg.content.length) {
                msg.content = this.default_message;
            }
            this.$http.post(baseUrl + '/api/message', msg).then(function (res) {
                if (res.status === 200) {
                    vm.errorMessage = '生成成功';
                    vm.showError = true;
                    setTimeout(function () {
                        vm.showError = false;
                        window.location.href = baseUrl + '/message/' + res.data.id + '?s=preview';
                    }, 1000);
                    return false;
                }
            }, function (e) {
                console.error(e);
            })
        }
    },
    mounted: function () {
        this.$http.get(baseUrl + '/api/user').then(function (res) {
            this.user = res.data;
            if (!this.user) {
                var back = [window.location.pathname, window.location.search].join('');
                window.location.href = [baseUrl + '/wx/login', '?back=', encodeURIComponent(back)].join('');
                return false;
            } else {
                // 已经登录，签名js-sdk
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
                    console.log('bad signature', e);
                });
            }
        }, function (e) {
            console.error(e);
        });
    }
});
wx.ready(function () {
   wx.hideAllNonBaseMenuItem();
});