<template>
    <v-card outlined>
        <v-card-title>
            Payment # {{item._links.self.href.split("/")[item._links.self.href.split("/").length - 1]}}
        </v-card-title>

        <v-card-text>
            <div>
                <Number label="OrderId" v-model="item.orderId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="PaymentStatus" v-model="item.paymentStatus" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Number label="Price" v-model="item.price" :editMode="editMode" @change="change" />
            </div>
        </v-card-text>
    </v-card>
</template>


<script>
    const axios = require('axios').default;

    export default {
        name: 'PaymentDetail',
        components:{},
        props: {
        },
        data: () => ({
            item: null,
        }),
        async created() {
            var me = this;
            var params = this.$route.params;
            var temp = await axios.get(axios.fixUrl('/payments/' + params.id))
            if(temp.data) {
                me.item = temp.data
            }
        },
        methods: {
        },
    };
</script>
