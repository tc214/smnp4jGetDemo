package com.tc.snmp;

import java.io.IOException;
import java.util.Vector;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


public class SnmpUtil {  
    private Snmp snmp = null;  
    private Address targetAddress = null;  
  
    public void initComm() throws IOException {  
        // set Agent's IP and port  
        targetAddress = GenericAddress.parse("udp:127.0.0.1/161");  
        TransportMapping transport = new DefaultUdpTransportMapping();  
        snmp = new Snmp(transport);  
        transport.listen();  
    }  
  
    public void sendPDU() throws IOException {  
        // set target  
	    CommunityTarget target = new CommunityTarget();  
	    target.setCommunity(new OctetString("public"));  
	    target.setAddress(targetAddress);  
	    target.setType(1);
	    // retry time after failed  
	    target.setRetries(2);  
	    // timeout, unit:s
	    target.setTimeout(35000);  
	    target.setVersion(SnmpConstants.version1);  
	    // create PDU  
	    PDU pdu = new PDU();  
	    pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 5, 0 })));  
	    // MIB's type  
	    pdu.setType(PDU.GET);  
	    // send PDU to Agent, receiving Response  
        ResponseEvent respEvnt = snmp.send(pdu, target);  
  
        // parse Response  
        if (respEvnt != null && respEvnt.getResponse() != null) {  
            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse().getVariableBindings();  
            for (int i = 0; i < recVBs.size(); i++) {  
            	VariableBinding recVB = recVBs.elementAt(i);  
            	System.out.println(recVB.getOid() + " : " + recVB.getVariable());  
            }  
        }  
    }  
   
    public static void main(String[] args) {  
        try {  
           SnmpUtil snmpUtil = new SnmpUtil();  
           snmpUtil.initComm();  
           snmpUtil.sendPDU();  
        } catch (IOException e) {  
           e.printStackTrace();  
        }  
    }  
}
