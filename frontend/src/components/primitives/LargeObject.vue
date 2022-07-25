<template>
    <div>
        <div v-if="editMode" style="margin-top:-20px;">
            <v-checkbox
                    v-model="isFile"
                    label="Attachment File"
            ></v-checkbox>
            <v-text-field
                    v-if="!isFile"
                    :label="label" 
                    :type="type"
                    v-model="value"
                    @change="change"
            ></v-text-field>
            <v-file-input
                    v-else
                    :label="label"
                    v-model="value"
                    @change="change"
            ></v-file-input>
        </div>
        <div v-else>
            {{label}} :  {{value}}
        </div>
    </div>
</template>

<script>  
    export default {
        name: 'LargeObject',
        components:{
        },
        data: () => ({
            isFile: false,
            type: this.value,
        }),
        props: {
            value: [String, Number],
            editMode: Boolean,
            label: String
        },
        created() {
            if(typeof this.value === 'string') {
                this.type = 'text';
            } else if(typeof this.value === 'number') {
                this.type = 'number';
            }
        },
        methods:{
            change(){
                this.$emit("input", this.value);
            }
        }
    }
</script>
