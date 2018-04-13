package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SmartcodeTxTest {

    OntSdk ontSdk;
    Identity did;
    AbiFunction func;

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20384");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("SmartcodeTxTest.json");


        ontSdk.setCodeAddress("80a45524f3f6a5b98d633e5c7a7458472ec5d625");
        InputStream is = new FileInputStream("/Users/sss/dev/ontologytest/IdContract/IdContract.abi.json");
        byte[] bys = new byte[is.available()];
        is.read(bys);
        is.close();
        String abi = new String(bys);

        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);

        if (ontSdk.getWalletMgr().getIdentitys().size() == 0) {
            Map map = new HashMap<>();
            map.put("test", "value00");
            Identity did = ontSdk.getOntIdTx().sendRegister("passwordtest");
        }
        did = ontSdk.getWalletMgr().getIdentitys().get(0);


        AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(did.ontid, "passwordtest");
        func = abiinfo.getFunction("AddAttribute");
        System.out.println(func.getName() + ":  " + func.getParameters());
        String value = "{\"Context\":\"claim:context\",\"Content\":{\"Issuer\":\"did:ont:TA8WyMDTP7BXFK6pVZ55Wn9gvAjCvsMfTm\",\"Subject\":\"did:ont:TA7idxkVHWXQgk2fd4dPZRyp7V2G7qTBzd\"},\"Signature\":{\"Format\":\"pgp\",\"Value\":\"AXLttwbJT8PV3L8P721PEDSFrMZ+wf4tYEcQMfsBiDH9Wi3DUvUnLdeBkarH2ZcvqB3YICFBcJy8aA46VLjFkFA=\",\"Algorithm\":\"ECDSAwithSHA256\"},\"Metadata\":{\"Issuer\":\"did:ont:TA8WyMDTP7BXFK6pVZ55Wn9gvAjCvsMfTm\",\"CreateTime\":\"2018-03-29T16:45:09Z\",\"Subject\":\"did:ont:TA7idxkVHWXQgk2fd4dPZRyp7V2G7qTBzd\"},\"Id\":\"3903a2f8158d71b976936ab60656fc0b615be40715d9235e4992c15e530b8ed3\"}";
        func.setParamsValue(did.ontid.getBytes(), "key".getBytes(), "bytes".getBytes(), value.getBytes(), Helper.hexToBytes(info.pubkey));
    }

    @Test
    public void sendInvokeSmartCode() throws Exception {


        ontSdk.getSmartcodeTx().sendInvokeSmartCode(did.ontid,"passwordtest",func,(byte) VmType.NEOVM.value());
    }

    @Test
    public void sendInvokeSmartCodeWithSign() throws Exception {

        String hash = ontSdk.getSmartcodeTx().sendInvokeSmartCodeWithSign(did.ontid, "passwordtest", func, (byte) VmType.NEOVM.value());

    }

    @Test
    public void invokeTransactionPreExec() throws Exception {

        ontSdk.getSmartcodeTx().sendInvokeTransactionPreExec(did.ontid, "passwordtest", func, (byte) VmType.NEOVM.value());
    }

    @Test
    public void deployCodeTransaction() throws IOException, SDKException, ConnectorException {
        InputStream is = new FileInputStream("/Users/sss/dev/ontologytest/IdContract/IdContract.avm");
        byte[] bys = new byte[is.available()];
        is.read(bys);
        is.close();

        String code = "";
        code = Helper.toHexString(bys);
        System.out.println("CodeAddress:" + Helper.getCodeAddress(code, VmType.NEOVM.value()));

        ontSdk.setCodeAddress(Helper.getCodeAddress(code, VmType.NEOVM.value()));

        Transaction tx = ontSdk.getSmartcodeTx().makeDeployCodeTransaction(code, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value());
        String txHex = Helper.toHexString(tx.toArray());
        System.out.println(txHex);
        ontSdk.getConnectMgr().sendRawTransaction(txHex);

        System.out.println("txhash:" + tx.hash().toString());
    }

    @Test
    public void createCodeParamsScript() {
        List list = new ArrayList<Object>();
        list.add("".getBytes());

        ontSdk.getSmartcodeTx().createCodeParamsScript(list);
    }

    @Test
    public void makeDeployCodeTransaction() throws SDKException {

        Transaction tx = ontSdk.getSmartcodeTx().makeDeployCodeTransaction("0136c56b6c766b00527ac46c766b51527ac4616c766b00c3125265674964576974685075626c69634b6579876c766b52527ac46c766b52c3645d00616c766b51c3c0529c009c6c766b55527ac46c766b55c3640e00006c766b56527ac46204066c766b51c300c36c766b53527ac46c766b51c351c36c766b54527ac46c766b53c36c766b54c3617c65e2056c766b56527ac462cf056c766b00c31352656749645769746841747472696275746573876c766b57527ac46c766b57c3647100616c766b51c3c0539c009c6c766b5b527ac46c766b5bc3640e00006c766b56527ac46281056c766b51c300c36c766b58527ac46c766b51c351c36c766b59527ac46c766b51c352c36c766b5a527ac46c766b58c36c766b59c36c766b5ac3615272656b066c766b56527ac46238056c766b00c3064164644b6579876c766b5c527ac46c766b5cc3647100616c766b51c3c0539c009c6c766b60527ac46c766b60c3640e00006c766b56527ac462f7046c766b51c300c36c766b5d527ac46c766b51c351c36c766b5e527ac46c766b51c352c36c766b5f527ac46c766b5dc36c766b5ec36c766b5fc361527265260a6c766b56527ac462ae046c766b00c30952656d6f76654b6579876c766b0111527ac46c766b0111c3647900616c766b51c3c0539c009c6c766b0115527ac46c766b0115c3640e00006c766b56527ac46266046c766b51c300c36c766b0112527ac46c766b51c351c36c766b0113527ac46c766b51c352c36c766b0114527ac46c766b0112c36c766b0113c36c766b0114c361527265d00a6c766b56527ac46217046c766b00c30c416464417474726962757465876c766b0116527ac46c766b0116c364b500616c766b51c3c0559c009c6c766b011c527ac46c766b011cc3640e00006c766b56527ac462cc036c766b51c300c36c766b0117527ac46c766b51c351c36c766b0118527ac46c766b51c352c36c766b0119527ac46c766b51c353c36c766b011a527ac46c766b51c354c36c766b011b527ac46c766b0117c36c766b0118c36c766b0119c36c766b011ac36c766b011bc3615479517956727551727553795279557275527275650e0d6c766b56527ac46241036c766b00c30f52656d6f7665417474726962757465876c766b011d527ac46c766b011dc3647900616c766b51c3c0539c009c6c766b0121527ac46c766b0121c3640e00006c766b56527ac462f3026c766b51c300c36c766b011e527ac46c766b51c351c36c766b011f527ac46c766b51c352c36c766b0120527ac46c766b011ec36c766b011fc36c766b0120c3615272654a0e6c766b56527ac462a4026c766b00c30b4164645265636f76657279876c766b0122527ac46c766b0122c3647900616c766b51c3c0539c009c6c766b0126527ac46c766b0126c3640e00006c766b56527ac4625a026c766b51c300c36c766b0123527ac46c766b51c351c36c766b0124527ac46c766b51c352c36c766b0125527ac46c766b0123c36c766b0124c36c766b0125c361527265080a6c766b56527ac4620b026c766b00c30e4368616e67655265636f76657279876c766b0127527ac46c766b0127c3647900616c766b51c3c0539c009c6c766b012b527ac46c766b012bc3640e00006c766b56527ac462be016c766b51c300c36c766b0128527ac46c766b51c351c36c766b0129527ac46c766b51c352c36c766b012a527ac46c766b0128c36c766b0129c36c766b012ac361527265650a6c766b56527ac4626f016c766b00c3114164644174747269627574654172726179876c766b012c527ac46c766b012cc364050061616c766b00c30d4765745075626c69634b657973876c766b012d527ac46c766b012dc3644d00616c766b51c3c0519c009c6c766b012f527ac46c766b012fc3640e00006c766b56527ac462f8006c766b51c300c36c766b012e527ac46c766b012ec36165230e6c766b56527ac462d5006c766b00c30d47657441747472696275746573876c766b0130527ac46c766b0130c3644d00616c766b51c3c0519c009c6c766b0132527ac46c766b0132c3640e00006c766b56527ac46289006c766b51c300c36c766b0131527ac46c766b0131c36165280f6c766b56527ac46266006c766b00c30647657444444f876c766b0133527ac46c766b0133c3643d00616c766b51c300c36c766b0134527ac46c766b51c351c36c766b0135527ac46c766b0134c36c766b0135c3617c652b116c766b56527ac4620e00006c766b56527ac46203006c766b56c3616c756657c56b6c766b00527ac46c766b51527ac4616c766b00c3616502146c766b52527ac46c766b52c3c0519f630f006c766b52c361654911620400516c766b53527ac46c766b53c3640e00006c766b54527ac462c6006c766b51c36168184e656f2e52756e74696d652e436865636b5769746e657373009c6c766b55527ac46c766b55c3640e00006c766b54527ac4628a006c766b52c36c766b51c3617c6546146c766b56527ac46c766b56c3646100616c766b52c361653f11616c766b52c300617c65c711616c766b52c351617c655c12610872656769737465726c766b00c3617c08526567697374657253c168124e656f2e52756e74696d652e4e6f7469667961516c766b54527ac4620e00006c766b54527ac46203006c766b54c3616c7566011ec56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c36165da126c766b53527ac46c766b53c3c0519f630f006c766b53c361652110620400516c766b5d527ac46c766b5dc3640e00006c766b5e527ac462e3036c766b51c36168184e656f2e52756e74696d652e436865636b5769746e657373009c6c766b5f527ac46c766b5fc3640e00006c766b5e527ac462a7036c766b52c3c0640e006c766b52c300517f620400006c766b5a527ac46c766b52c3c051946c766b5b527ac4020001c56c766b5c527ac4006c766b54527ac4516c766b55527ac4006c766b56527ac462c902616c766b5bc3529f6c766b0115527ac46c766b0115c3641100516c766b56c3966c766b56527ac46c766b52c36c766b55c3517f020001956c766b52c36c766b55c35193517f936c766b60527ac46c766b5bc36c766b60c352939f6c766b0116527ac46c766b0116c3641100516c766b56c3966c766b56527ac46c766b52c36c766b55c352936c766b60c37f6c766b0111527ac46c766b0111c3c051a0009c6c766b0117527ac46c766b0117c3641100516c766b56c3966c766b56527ac46c766b0111c300517f6c766b57527ac46c766b0111c3c0516c766b57c393a0009c6c766b0118527ac46c766b0118c3641100516c766b56c3966c766b56527ac46c766b0111c3516c766b57c393517f6c766b58527ac46c766b0111c3c0546c766b57c3936c766b58c3939f6c766b0119527ac46c766b0119c3641100516c766b56c3966c766b56527ac46c766b0111c3526c766b57c3936c766b58c393517f020001956c766b0111c3536c766b57c3936c766b58c393517f936c766b59527ac46c766b57c36c766b58c3936c766b59c39354936c766b60c39c009c6c766b011a527ac46c766b011ac3641100516c766b56c3966c766b56527ac46c766b0111c3516c766b57c37f6c766b0112527ac46c766b0111c3526c766b57c3936c766b58c37f6c766b0113527ac46c766b0111c3546c766b57c3936c766b58c3936c766b59c37f6c766b0114527ac46c766b58c302ff00a06c766b011b527ac46c766b011bc3640e00006c766b5e527ac46229016c766b53c36c766b0112c3617c650b116c766b011c527ac46c766b011cc36433006c766b53c36c766b0112c36c766b0113c36c766b0114c3615379517955727551727552795279547275527275659915616c766b5cc36c766b54c36c766b0112c3c46c766b55c352936c766b60c3936c766b55527ac46c766b5bc3526c766b60c393946c766b5b527ac4616c766b54c351936c766b54527ac46c766b54c36c766b5ac39f6c766b011d527ac46c766b011dc36321fd6c766b53c36165030d616c766b53c36c766b51c3617c65e10f756c766b53c351617c651c0e616c766b53c36c766b54c3617c656b0d610872656769737465726c766b00c3617c08526567697374657253c168124e656f2e52756e74696d652e4e6f7469667961516c766b5e527ac46203006c766b5ec3616c756659c56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c36165960e6c766b53527ac46c766b53c3c0519f6311006c766b53c36165dd0b009c620400516c766b55527ac46c766b55c3640e00006c766b56527ac462de006c766b52c36168184e656f2e52756e74696d652e436865636b5769746e6573736417006c766b53c36c766b52c3617c65f606009c620400516c766b57527ac46c766b57c3640e00006c766b56527ac4628c006c766b53c36165be0c6c766b54527ac46c766b53c36c766b51c3617c65b20e6c766b58527ac46c766b58c3645300616c766b53c36c766b54c35193617c65d80c61036164646c766b00c36c766b51c3615272095075626c69634b657954c168124e656f2e52756e74696d652e4e6f7469667961516c766b56527ac4620e00006c766b56527ac46203006c766b56c3616c756659c56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c36165550d6c766b53527ac46c766b53c3c0519f6311006c766b53c361659c0a009c620400516c766b55527ac46c766b55c3640e00006c766b56527ac462e1006c766b52c36168184e656f2e52756e74696d652e436865636b5769746e6573736417006c766b53c36c766b52c3617c65b505009c620400516c766b57527ac46c766b57c3640e00006c766b56527ac4628f006c766b53c361657d0b6c766b54527ac46c766b53c36c766b51c3617c65a70d6c766b58527ac46c766b58c3645600616c766b53c36c766b54c35194617c65970b610672656d6f76656c766b00c36c766b51c3615272095075626c69634b657954c168124e656f2e52756e74696d652e4e6f7469667961516c766b56527ac4620e00006c766b56527ac46203006c766b56c3616c756658c56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c36165110c6c766b53527ac46c766b53c3c0519f6311006c766b53c361655809009c620400516c766b54527ac46c766b54c3640e00006c766b55527ac46296006c766b52c36168184e656f2e52756e74696d652e436865636b5769746e6573736417006c766b53c36c766b52c3617c657104009c620400516c766b56527ac46c766b56c3640e00006c766b55527ac46244006c766b53c36165da0ac000a06c766b57527ac46c766b57c3640e00006c766b55527ac4621e006c766b53c36c766b51c3617c65ff0a61516c766b55527ac46203006c766b55c3616c756657c56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c36165180b6c766b53527ac46c766b53c3c0519f6311006c766b53c361655f08009c620400516c766b54527ac46c766b54c3640e00006c766b55527ac46274006c766b52c36c766b53c361652e0a617c6594116428006c766b52c36168184e656f2e52756e74696d652e436865636b5769746e657373009c620400516c766b56527ac46c766b56c3640e00006c766b55527ac4621e006c766b53c36c766b51c3617c65280a61516c766b55527ac46203006c766b55c3616c75665bc56b6c766b00527ac46c766b51527ac46c766b52527ac46c766b53527ac46c766b54527ac4616c766b00c36165330a6c766b55527ac46c766b55c3c0519f6311006c766b55c361657a07009c620400516c766b56527ac46c766b56c3640e00006c766b57527ac46268016c766b54c36168184e656f2e52756e74696d652e436865636b5769746e6573736417006c766b55c36c766b54c3617c659302009c620400516c766b58527ac46c766b58c3640e00006c766b57527ac46216016c766b55c36c766b51c3617c65cb0a6c766b59527ac46c766b59c3648900616c766b55c361659b076c766b5a527ac46c766b55c36c766b5ac35193617c65d407616c766b55c36c766b51c36c766b52c36c766b53c3615379517955727551727552795279547275527275653b0f61036164646c766b00c36c766b51c36152720941747472696275746554c168124e656f2e52756e74696d652e4e6f746966796161626700616c766b55c36c766b51c36c766b52c36c766b53c361537951795572755172755279527954727552727565d70e61067570646174656c766b00c36c766b51c36152720941747472696275746554c168124e656f2e52756e74696d652e4e6f746966796161516c766b57527ac46203006c766b57c3616c756659c56b6c766b00527ac46c766b51527ac46c766b52527ac4616c766b00c3616568086c766b53527ac46c766b53c3c0519f6311006c766b53c36165af05009c620400516c766b54527ac46c766b54c3640e00006c766b55527ac462f1006c766b52c36168184e656f2e52756e74696d652e436865636b5769746e6573736417006c766b53c36c766b52c3617c65c800009c620400516c766b56527ac46c766b56c3640e00006c766b55527ac4629f006c766b53c36c766b51c3617c6536096c766b57527ac46c766b57c3647600616c766b53c36165d0056c766b58527ac46c766b53c36c766b58c35194617c650906616c766b53c36c766b51c3617c65020e610672656d6f76656c766b53c36c766b51c36152720941747472696275746554c168124e656f2e52756e74696d652e4e6f7469667961516c766b55527ac4620e00006c766b55527ac46203006c766b55c3616c756654c56b6c766b00527ac46c766b51527ac4616c766b00c3527e6c766b51c3617c65240ac0009c6c766b52527ac46c766b52c3640e00006c766b53527ac4620e00516c766b53527ac46203006c766b53c3616c75665dc56b6c766b00527ac4616c766b00c36165ce066c766b51527ac400006c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b51c3527e617c680f4e656f2e53746f726167652e4765746c766b54527ac46c766b54c3c0009c6c766b58527ac46c766b58c3640e00006c766b59527ac462ee006c766b54c36c766b55527ac46c766b55c36165170b6c766b52527ac4629300616168164e656f2e53746f726167652e476574436f6e746578746c766b51c3527e6c766b55c37e617c680f4e656f2e53746f726167652e4765746c766b5a527ac46c766b5ac3616536086c766b55527ac46c766b53c351936c766b53527ac46c766b55c3c0009c6c766b5b527ac46c766b5bc36406006225006c766b52c36c766b55c36165860a7e6c766b52527ac461516c766b5c527ac46268ff6c766b53c3616596096c766b56527ac46c766b56c36c766b52c37e6c766b57527ac46c766b57c36c766b59527ac46203006c766b59c3616c75665cc56b6c766b00527ac4616c766b00c361655a056c766b51527ac400006c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b51c3557e617c680f4e656f2e53746f726167652e4765746c766b54527ac46c766b54c3c0009c6c766b55527ac46c766b55c3640f0061006c766b56527ac4627301616c766b54c36c766b57527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b51c3567e6c766b57c37e617c680f4e656f2e53746f726167652e4765746c766b58527ac46c766b57c3616561096c766b58c3616558097e616553096c766b52527ac462e100616168164e656f2e53746f726167652e476574436f6e746578746c766b51c3557e6c766b57c37e617c680f4e656f2e53746f726167652e4765746c766b59527ac46c766b59c3616572066c766b57527ac46c766b53c351936c766b53527ac46c766b57c3c0009c6c766b5a527ac46c766b5ac36406006273006168164e656f2e53746f726167652e476574436f6e746578746c766b51c3567e6c766b57c37e617c680f4e656f2e53746f726167652e4765746c766b58527ac46c766b52c36c766b57c3616582086c766b58c3616579087e616574087e6c766b52527ac461516c766b5b527ac4621aff6c766b53c3616584076c766b52c37e6c766b56527ac46203006c766b56c3616c756653c56b6c766b00527ac4616c766b00c3616560036c766b51527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b51c3577e617c680f4e656f2e53746f726167652e4765746c766b52527ac46203006c766b52c3616c756656c56b6c766b00527ac46c766b51527ac4616c766b00c3616519fc6c766b52527ac46c766b00c361657dfd6c766b53527ac46c766b00c3616567ff6c766b54527ac46c766b52c3616592076c766b53c3616589077e6c766b54c361657f077e6c766b55527ac46203006c766b55c3616c756653c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b51527ac46c766b51c3c0641b006c766b51c300517f5100517f9c6307000062040051620400006c766b52527ac46203006c766b52c3616c756651c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c351615272680f4e656f2e53746f726167652e50757461616c756652c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3547e617c680f4e656f2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756652c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3547e6c766b51c3615272680f4e656f2e53746f726167652e50757461616c756652c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3517e617c680f4e656f2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756652c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3517e6c766b51c3615272680f4e656f2e53746f726167652e50757461616c756652c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3577e617c680f4e656f2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756652c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3577e6c766b51c3615272680f4e656f2e53746f726167652e50757461616c756653c56b6c766b00527ac4616c766b00c3c06410006c766b00c3c002ff00a0620400516c766b51527ac46c766b51c3640e00006c766b52527ac4621d006c766b00c3c061651d0b6c766b00c37e6c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac4616c766b00c3c0519f6319006c766b00c3c06c766b00c300517f51939c009c620400516c766b51527ac46c766b51c3640e00006c766b52527ac4621c006c766b00c3516c766b00c300517f7f6c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c3527e6c766b51c3617c652a066c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c3527e6c766b51c3617c6584076c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c3557e6c766b51c3617c65be056c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c3557e6c766b51c3617c6518076c766b52527ac46203006c766b52c3616c756652c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b51527ac46203006c766b51c3616c756652c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b51c3615272680f4e656f2e53746f726167652e50757461616c756653c56b6c766b00527ac4616c766b00c300547f61651c016c766b51527ac46c766b00c3546c766b51c37f6c766b52527ac46203006c766b52c3616c756654c56b6c766b00527ac4616c766b00c300547f6165df006c766b51527ac46c766b00c36c766b51c35593547f6165c6006c766b52527ac46c766b00c3596c766b51c3936c766b52c37f6c766b53527ac46203006c766b53c3616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c36165dc0101017e6c766b51c36165d0017e6c766b52527ac46203006c766b52c3616c756653c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b51c37e617c680f4e656f2e53746f726167652e4765746c766b52527ac46203006c766b52c3616c756655c56b6c766b00527ac461006c766b51527ac4006c766b52527ac4623c00616c766b51c3020001956c766b51527ac46c766b51c36c766b00c36c766b52c3517f936c766b51527ac4616c766b52c351936c766b52527ac46c766b52c36c766b00c3c09f6c766b53527ac46c766b53c363afff6c766b51c36c766b54527ac46203006c766b54c3616c756657c56b6c766b00527ac4616c766b00c36c766b51527ac46c766b51c3020001976c766b52527ac46c766b51c36c766b52c394020001966c766b51527ac46c766b51c3020001976c766b53527ac46c766b51c36c766b53c394020001966c766b51527ac46c766b51c3020001976c766b54527ac46c766b51c36c766b54c394020001966c766b51527ac46c766b51c3020001976c766b55527ac46c766b55c36165ca066c766b54c36165c1067e6c766b53c36165b7067e6c766b52c36165ad067e6c766b56527ac46203006c766b56c3616c756657c56b6c766b00527ac4616c766b00c3c06c766b51527ac46c766b51c3020001976c766b52527ac46c766b51c36c766b52c394020001966c766b51527ac46c766b51c3020001976c766b53527ac46c766b51c36c766b53c394020001966c766b51527ac46c766b51c3020001976c766b54527ac46c766b51c36c766b54c394020001966c766b51527ac46c766b51c3020001976c766b55527ac46c766b55c36165f6056c766b54c36165ed057e6c766b53c36165e3057e6c766b52c36165d9057e6c766b00c37e6c766b56527ac46203006c766b56c3616c756653c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3567e6c766b51c37e617c680f4e656f2e53746f726167652e4765746c766b52527ac46203006c766b52c3616c756654c56b6c766b00527ac46c766b51527ac46c766b52527ac46c766b53527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3566c766b51c37e7e6c766b52c3c0616511056c766b52c36c766b53c37e7e615272680f4e656f2e53746f726167652e50757461616c756652c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3566c766b51c37e7e617c68124e656f2e53746f726167652e44656c65746561616c756653c56b6c766b00527ac46c766b51527ac4616c766b00c3c06c766b51c3c0907c907c9e6311006c766b00c36c766b51c39c620400006c766b52527ac46203006c766b52c3616c756658c56b6c766b00527ac46c766b51527ac4616c766b00c3616576fa6c766b52527ac46c766b00c36c766b51c3617c65d1fb6c766b53527ac46c766b00c36c766b52c3617c65bbfb6c766b54527ac46c766b53c300a06c766b55527ac46c766b55c3640e00006c766b56527ac4621b016c766b52c3009c6c766b57527ac46c766b57c3645800616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b51c37e0000617c6516fb615272680f4e656f2e53746f726167652e507574616c766b00c36c766b51c3617c650bfa616162a500616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b51c37e6c766b52c300617c65bdfa615272680f4e656f2e53746f726167652e507574616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b52c37e6c766b54c36165e2f96c766b51c3617c6571fa615272680f4e656f2e53746f726167652e507574616c766b00c36c766b51c3617c6566f96161516c766b56527ac46203006c766b56c3616c75665fc56b6c766b00527ac46c766b51527ac4616c766b00c36c766b51c3617c6551fa6c766b52527ac46c766b52c3c0009c6c766b56527ac46c766b56c3640e00006c766b57527ac4626e02006c766b53527ac46c766b52c3616580f96c766b54527ac46c766b52c3616533f96c766b55527ac46c766b54c3c0009c6c766b58527ac46c766b58c364a400616c766b55c3c0009c6c766b59527ac46c766b59c3641800616c766b00c36c766b53c3617c659ff86161627300616c766b00c36c766b55c3617c65acf96c766b5a527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b55c37e6c766b5ac36165a4f800617c6537f9615272680f4e656f2e53746f726167652e507574616c766b00c36c766b55c3617c652cf8616161624501616c766b55c3c0009c6c766b5b527ac46c766b5bc3646600616c766b00c36c766b54c3617c6520f96c766b5c527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b54c37e006c766b5cc3616554f8617c65abf8615272680f4e656f2e53746f726167652e507574616162c900616c766b00c36c766b55c3617c65bdf86c766b5d527ac46c766b00c36c766b54c3617c65a7f86c766b5e527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b54c37e6c766b55c36c766b5ec36165d7f7617c652ef8615272680f4e656f2e53746f726167652e507574616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b55c37e6c766b5dc3616553f76c766b54c3617c65e2f7615272680f4e656f2e53746f726167652e5075746161616168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b51c37e6c766b53c3615272680f4e656f2e53746f726167652e50757461516c766b57527ac46203006c766b57c3616c756653c56b6c766b00527ac4614d0001000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9fa0a1a2a3a4a5a6a7a8a9aaabacadaeafb0b1b2b3b4b5b6b7b8b9babbbcbdbebfc0c1c2c3c4c5c6c7c8c9cacbcccdcecfd0d1d2d3d4d5d6d7d8d9dadbdcdddedfe0e1e2e3e4e5e6e7e8e9eaebecedeeeff0f1f2f3f4f5f6f7f8f9fafbfcfdfeff6c766b51527ac46c766b51c36c766b00c3517f6c766b52527ac46203006c766b52c3616c7566", true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value());
    }

    @Test
    public void makeInvokeCodeTransaction() throws SDKException {

        List list = new ArrayList<Object>();
        list.add("test");
        byte[] params = ontSdk.getSmartcodeTx().createCodeParamsScript(list);
        Fee[] fees = new Fee[0];
        ontSdk.getSmartcodeTx().makeInvokeCodeTransaction("80a45524f3f6a5b98d633e5c7a7458472ec5d625",null,params, VmType.NEOVM.value(), fees);
    }
}