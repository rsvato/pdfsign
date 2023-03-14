;(function () {
    let Signer = function (file) {
        this.file = file;
        this.data = {};
    };
    Signer.prototype.sign = function () {
        if (!window.selectedCert) {
            alert("Please, select certificate first");
            return;
        }
        console.log("Let's sign " + this.file + ". Cert selected = " + window.selectedCert);
        let promise = this.fetchData();
        let self = this;
        promise.then(function (t) {
            self.data = t;
            console.log(self.data);
            cadesplugin.async_spawn(function* () {
                let oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
                yield oStore.Open();

                let allCerts = yield oStore.Certificates;
                let cert = yield allCerts.Item(window.selectedCert);

                let signer = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
                yield signer.propset_Certificate(cert);

                let signedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
                if (self.data.hasOwnProperty("hash")) {
                    try {
                        yield signedData.propset_ContentEncoding(cadesplugin.CADESCOM_BASE64_TO_BINARY);
                        yield signedData.propset_Content(self.data.hash);
                        let signature = yield signedData.SignCades(signer, cadesplugin.CADESCOM_CADES_BES, true);
                        console.log(signature);
                        self.sendSignature(self.data.field, signature).then(response => response.status)
                    } catch (err) {
                        throw "Can't create signature due to " + cadesplugin.getLastError(err);
                    }
                }
            })
        })
    }
    Signer.prototype.fetchData = async function () {
        return fetch("/files/" + this.file + "/hash").then(response => response.json())
    }
    Signer.prototype.sendSignature = async function(field, hash) {
        return fetch("/files/" + this.file + "/" + field + "/sign", {method:"put", body: hash})
    }
    window.signer = Signer;
})();


