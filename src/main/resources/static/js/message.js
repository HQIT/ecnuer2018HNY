const MAX_MSG_LEN = 120;
var config = {
    el: '#app',
    data: {
        index: 0,
        blessings: [],
        isLoading: true
    },
    computed: {
        sentDate: function () {
            var date = new Date(this.timestamp);
            return [
                date.getFullYear(), '/',
                date.getMonth() + 1, '/',
                date.getDate(), ' ',
                date.getHours(), ':', date.getMinutes()
            ].join('');
        }
    },
    created: function () {
        Vue.http.get(baseUrl + '/api/user').then(function (res) {
            _USER = res.data;
            if (!_USER) {
                var back = [window.location.pathname, window.location.search].join('');
                window.location.href = [baseUrl + '/wx/login', '?back=', encodeURIComponent(back)].join('');
                return false;
            } else {
                on_login_success();
            }
        }, function (e) {
            console.error(e);
        });
    },
    methods: {
        send: function () {
            window.location.href = baseUrl + '/message/editor';
            return false;
        },
        switchtab: function (index) {
            this.index = index;
            this.getMessage(index);
        },
        getMessage: function (index) {
            var vm = this;
            vm.isLoading = true;
            vm.blessings = [];
            this.$http.get(baseUrl + `/api/message?f=${index === 0 ? 'by-sender' : 'by-receiver'}`).then(function (res) {
                vm.isLoading = false;
                if (res.status === 200) {
                    vm.blessings = res.data;
                }
            }, function (e) {
                vm.isLoading = false;
                console.error(e);
            });
        },
        detail: function (type, id) {
            if (type === 0) {
                window.location.href = baseUrl + `/message/statistic/${id}`;
            } else {
                window.location.href = baseUrl + `/message/${id}?s=received`;
            }
        },
        share: function () {
            window.location.href = baseUrl + '/message/editor';
        }
    },
    components: {
       'sent-blessing': {
           name: 'sent-blessing',
           template: `<div class="blessing-wrp slide-anim" v-on:click="detail(id)"><div class="inline">
    <span>第{{ith}}个祝福</span><span v-text="sentDate"></span>
</div>
<div class="inline reverse">
<div class="mock-text"><span>已经有</span><span v-text="receivers.length" class="highlight"></span><span>位校友收到祝福</span></div> 
<div class="receiver-wrp"><img v-for="receiver in recentReceivers" class="receiver"  v-bind:src="receiver.portrait"></div>
</div></div>`,
           data: function () {
               return {
               }
           },
           props: {
               id: { type: String, default: '' },
               ith: { type: Number, default: 0 },
               timestamp: { type: Number, default: 0 },
               receivers: { type: Array, default: function () {
                       return [];
                   }}
           },
           methods: {
               detail: function (index) {
                   this.$emit('detail', 0, index);
               }
           },
           computed: {
               sentDate: function () {
                   var date = new Date(this.timestamp);
                   return [
                       date.getFullYear(), '/',
                       date.getMonth() + 1, '/',
                       date.getDate()].join('');
               },
               recentReceivers: function () {
                   if (this.receivers.length > 3) {
                       return this.receivers.slice(this.receivers.length - 3, this.receivers.length).reverse();
                   } else {
                       return this.receivers.reverse();
                   }
               }
           }
       },
        'received-blessing': {
            name: 'received-blessing',
            template: `<div class="blessing-wrp slide-anim" v-on:click="detail(id)"><div class="inline">
    <span>第{{ith}}个祝福</span><span v-text="sentDate"></span>
</div>
<div class="inline reverse">
<div class="mock-text"><span v-text="senderName"></span><span>发来的祝福</span></div>
<div class="receiver-wrp"><img v-bind:src="senderAvatar" class="receiver"></div>
</div></div>`,
            data: function () {
                return {}
            },
            methods: {
              detail: function (index) {
                  this.$emit('detail', 1, index);
              }
            },
            props: {
                id: { type: String, default: '' },
                ith: { type: Number, default: 0 },
                timestamp: { type: Number, default: 0 },
                senderName: { type: String, default: '' },
                senderAvatar: { type: String, default: '' }
            },
            computed: {
                sentDate: function () {
                    var date = new Date(this.timestamp);
                    return [
                        date.getFullYear(), '/',
                        date.getMonth() + 1, '/',
                        date.getDate()].join('');
                }
            }
        }
    }
};
app = new Vue(config);

var on_login_success = function () {
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
    app.getMessage(0);
};
wx.ready(function () {
    wx.hideAllNonBaseMenuItem();
});