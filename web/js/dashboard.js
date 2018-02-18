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
        scenarioName: '',
        responseMessage: '',
        serviceId: '',
        id: '',
        ajaxRequest: false,
        postResults: []
    },
    methods: {
        onMockScenarioAddAction: function(event) {

            // POST /someUrl
            this.$http.post('http://localhost:8080/api/scenario/update', {
                    scenarioName: this.scenarioName,
                    responseMessage: this.responseMessage,
                    id: this.id,
                    serviceId: this.serviceId
                }).then(response => {

                // get status
                response.status;

            // get status text
            response.statusText;

            // get 'Expires' header
            response.headers.get('Expires');

            // get body data
            this.mockscenario = response.body;

        }, response => {
                // error callback
            });

            /*

            alert("yy" + this.scenarioName);
            this.ajaxRequest = true;
            this.$http.post('http://localhost:8080/api/scenario/update', {

                scenarioName: this.scenarioName,
                responseMessage: this.responseMessage,
                id: this.id,
                serviceId: this.serviceId
            }, function (data, status, request) {
                alert("Herex");
                //this.postResults = data;
                //this.ajaxRequest = false;
            });

                           */

        },
        onMockScenarioCreateForm: function() {
            this.mockscenario = [];
        },
        onMockScenarioSelection: function(mockScenario) {
            this.mockscenario = mockScenario;
            this.scenarioName = this.mockscenario.scenarioName;
            this.responseMessage = this.mockscenario.responseMessage;
            this.id = this.mockscenario.id;
            this.serviceId = this.mockscenario.serviceId;
        },
        onMockServiceSelection: function(mockService) {
            
           this.mockscenarios = mockService.scenarios;


        },
        fetchData() {
            axios.get('http://localhost:8080/api/service/list').then(response => {
                this.mockservices = response.data;
        });
        }
    }
});
