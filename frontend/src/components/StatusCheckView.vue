<template>

    <v-data-table
        :headers="headers"
        :items="statusCheck"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'StatusCheckView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
                { text: "status", value: "status" },
                { text: "orderId", value: "orderId" },
            ],
            statusCheck : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/statusChecks'))

            temp.data._embedded.statusChecks.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.statusCheck = temp.data._embedded.statusChecks;
        },
        methods: {
        }
    }
</script>

