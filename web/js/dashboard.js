var app = new Vue({
    el: '#app',
    created() {
        this.fetchData();
    },
    data: {

        collapsed: true,
        mockservices: [],
        mockscenarios: [],
        mockscenario:[],
        debug: true,
        domain: '',
        ajaxRequest: false,
        postResults: []
    },
    methods: {
        onMockScenarioAddAction: function() {
            this.mockscenario = [];
            this.ajaxRequest = true;
            this.$http.post('http://localhost:8080/api/mockservicelist/', {
                domain: this.domain
            }, function (data, status, request) {
                this.postResults = data;
                this.ajaxRequest = false;
            });
            alert("yeah");
        },
        onMockScenarioCreateForm: function() {
            this.mockscenario = [];
        },
        onMockScenarioSelection: function(mockScenario) {
            this.mockscenario = mockScenario;
        },
        onMockServiceSelection: function(mockService) {
            
           this.mockscenarios = mockService.scenarios;


        },
        fetchData() {
            axios.get('http://localhost:8080/api/mockservicelist').then(response => {
                this.mockservices = response.data;
        });
        }
    }
});
