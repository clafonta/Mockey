var app = new Vue({
    el: '#app',
    created() {
        this.fetchData();
    },
    data: {
        collapsed: true,
        posts: []
    },
    methods: {
        fetchData() {
            axios.get('http://localhost:8080/rest/mockservicelist').then(response => {
                this.posts = response.data;
        });
        }
    }
});
