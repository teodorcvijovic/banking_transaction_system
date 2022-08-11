/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.klijent;

/**
 *
 * @author Teodor
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Klijent {
    
    private char intToChar(String si){
        int i = Integer.parseInt(si);
        return (char) i;
    }
    
    //////////////////////////////////////////
   
    public void run(){
        BufferedReader ulaz = new BufferedReader(new InputStreamReader(System.in));
        int zahtev;
        
        System.out.println("--------------------[ OPCIJE ]---------------------");
        System.out.println("\t1.  kreiranje mesta");
        System.out.println("\t2.  kreiranje filijale u mestu");
        System.out.println("\t3.  kreiranje komitenta");
        System.out.println("\t4.  promena sedista komitentu");
        System.out.println("\t5.  otvaranje racuna");
        System.out.println("\t6.  zatvaranje racuna");
        System.out.println("\t7.  transfer novca");
        System.out.println("\t8.  uplata");
        System.out.println("\t9.  isplata");
        System.out.println("\t10. dohvatanje svih mesta");
        System.out.println("\t11. dohvatanje svih filijala");
        System.out.println("\t12. dohvatanje svih komitenata");
        System.out.println("\t13. dohvatanje svih racuna komitenta");
        System.out.println("\t14. dohvatanje svih transakcija racuna");
        System.out.println("\t15. dohvatanje podataka iz rezervne kopije");
        System.out.println("\t16. dohvatanje razlike izmedju originalnih i rezervnih podataka");
        System.out.println("\t0.  zavrsi rad");
        System.out.println("---------------------------------------------------");
       
        String naziv, postbr, adresa, mesto, sediste, svrha;
        int idkom, idmes, dozvminus, idrac, idracsa, idracna, iznos, idfil;
        
        while(true){
            System.out.println("Unesite broj opcije: ");
            try {
                zahtev = Integer.parseInt(ulaz.readLine());
            } catch (IOException ex) { zahtev = 0; }
            
            if (zahtev==0) break;
            
            try {
            switch(zahtev)
            {
                case 1:
                    System.out.println("Naziv mesta: ");
                    naziv = ulaz.readLine();
                    
                    System.out.println("Postanski broj: ");
                    postbr = ulaz.readLine();
                    
                    kreiranjeMesta(naziv, postbr);
                    break;
                case 2:
                    System.out.println("Naziv filijale: ");
                    naziv = ulaz.readLine();
                    
                    System.out.println("Adresa filijale: ");
                    adresa = ulaz.readLine();
                    
                    System.out.println("Mesto filijale: ");
                    mesto = ulaz.readLine();
                    
                    kreiranjeFilijaleUMestu(naziv, adresa, mesto);
                    break;
                case 3:
                    System.out.println("Naziv komitenta: ");
                    naziv = ulaz.readLine();
                    
                    System.out.println("Adresa komitenta: ");
                    adresa = ulaz.readLine();
                    
                    System.out.println("Sediste komitenta: ");
                    sediste = ulaz.readLine();
                    
                    kreiranjeKomitenta(naziv, adresa, sediste);
                    break;
                case 4:
                    System.out.println("ID komitenta: ");
                    idkom = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("Novo mesto sedista komitenta: ");
                    mesto = ulaz.readLine();
                    
                    promenaSedistaKomitenta(idkom, mesto);
                    break;
                case 5:
                    System.out.println("ID komitenta racuna: ");
                    idkom = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("ID mesta u kom je otvoren racun: ");
                    idmes = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("Dozvoljeni minus: ");
                    dozvminus = Integer.parseInt(ulaz.readLine());
                    
                    otvoriRacun(idkom, idmes, dozvminus);
                    break;
                case 6:
                    System.out.println("ID racuna cije se zatvaranje zahteva: ");
                    idrac = Integer.parseInt(ulaz.readLine());
                    
                    zatvoriRacun(idrac);
                    break;
                case 7:
                    System.out.println("ID racuna platioca: ");
                    idracsa = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("ID racuna primaoca: ");
                    idracna = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("Iznos za transfer: ");
                    iznos = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("Svrha transakcije: ");
                    svrha = ulaz.readLine();
                    
                    transferNovca(idracsa, idracna, iznos, svrha);
                    break;
                case 8: case 9:
                    System.out.println("Iznos za " + (zahtev==8 ? "uplatu" : "isplatu") + ": ");
                    iznos = Integer.parseInt(ulaz.readLine());
                    iznos = (zahtev==8 ? iznos : -iznos);
                    
                    System.out.println("ID racuna: ");
                    idrac = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("ID filijale u kojoj se podnosi zahtev za transakciju: ");
                    idfil = Integer.parseInt(ulaz.readLine());
                    
                    System.out.println("Svrha transakcije: ");
                    svrha = ulaz.readLine();
                    
                    uplataIsplata(idrac, iznos, idfil, svrha);
                    break;
                case 10:
                    dohvMesta();
                    break;
                case 11:
                    dohvFilijale();
                    break;
                case 12:
                    dohvKomitente();
                    break;
                case 13:
                    System.out.println("ID komitenta ciji se racuni dohvataju: ");
                    idkom = Integer.parseInt(ulaz.readLine());
                    
                    dohvRacuneKomitenta(idkom);
                    break;
                case 14:
                    System.out.println("ID racuna cije se transakcije dohvataju: ");
                    idrac = Integer.parseInt(ulaz.readLine());
                    
                    dohvTransakcijeRacuna(idrac);
                    break;
                case 15:
                    Boolean razlika = false;
                    dohvPodatke(false);
                    break;
                case 16:
                    razlika = true;
                    dohvPodatke(true);
                    break;               
            }
            } catch (IOException ex) {}
        }
        
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Zavrsetak programa...");
    }
 
    public static void main(String[] args) {
        Klijent k = new Klijent();
        k.run();
    }

    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }
    
    private static String getCharacterDataFromElement(Element e, String field) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return getCharacterDataFromElement((Element) e.getElementsByTagName(field).item(0));
    }
    
    ///////////////////////////////////////////////////

    private void kreiranjeMesta(String naziv, String postbr) {
        String URLAddress = "http://localhost:8080/server/api/mesta";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                // Get an HttpURLConnection subclass object instead of URLConnection
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                // ensure you use the GET method
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
         
                // create the query params (remove when using GET)               
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("naziv=");
                queryParam.append(naziv);
                queryParam.append("&");
                queryParam.append("postbr=");
                queryParam.append(postbr);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void kreiranjeFilijaleUMestu(String naziv, String adresa, String mesto) {
        String URLAddress = "http://localhost:8080/server/api/filijale";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("naziv=");
                queryParam.append(naziv);
                queryParam.append("&");
                queryParam.append("adresa=");
                queryParam.append(adresa);
                queryParam.append("&");
                queryParam.append("mesto=");
                queryParam.append(mesto);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void kreiranjeKomitenta(String naziv, String adresa, String sediste) {
        String URLAddress = "http://localhost:8080/server/api/komitenti";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("naziv=");
                queryParam.append(naziv);
                queryParam.append("&");
                queryParam.append("adresa=");
                queryParam.append(adresa);
                queryParam.append("&");
                queryParam.append("sediste=");
                queryParam.append(sediste);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void promenaSedistaKomitenta(int idkom, String mesto) {
        String URLAddress = "http://localhost:8080/server/api/komitenti/sediste";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("IdKom=");
                queryParam.append(idkom);
                queryParam.append("&");
                queryParam.append("mesto=");
                queryParam.append(mesto);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void dohvMesta() {
        String URLAddress = "http://localhost:8080/server/api/mesta";
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			//System.out.println(inputString);
                    
			// parse xml
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));

			    Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("mesto");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);

                                /*
				NodeList idMes = element.getElementsByTagName("idMes");
				Element line = (Element) idMes.item(0);
				System.out.println("idMes: " + getCharacterDataFromElement(line));
                                */
                                
                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(0));
                                String postanskiBroj = getCharacterDataFromElement((Element) element.getElementsByTagName("postanskiBroj").item(0));
			
                                System.out.println("idMes: " + idMes + "\tnaziv: " + naziv + "\tpostanskiBroj: " + postanskiBroj);
                                
			    }
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }       
    }

    private void dohvFilijale() {
        String URLAddress = "http://localhost:8080/server/api/filijale";
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));

			    Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("filijala");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idFil = getCharacterDataFromElement((Element) element.getElementsByTagName("idFil").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(1));
                                String adresa = getCharacterDataFromElement((Element) element.getElementsByTagName("adresa").item(0));
                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0), "idMes");
                           
                                System.out.println("idFil: " + idFil + "\tnaziv: " + naziv + "\tadresa: " + adresa + "\tidMes: " + idMes);
                                
			    }
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }       
    }

    private void dohvKomitente() {
        String URLAddress = "http://localhost:8080/server/api/komitenti";
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));

			    Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("komitent");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idKom = getCharacterDataFromElement((Element) element.getElementsByTagName("idKom").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(1));
                                String adresa = getCharacterDataFromElement((Element) element.getElementsByTagName("adresa").item(0));
                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0), "idMes");
                           
                                System.out.println("idKom: " + idKom + "\tnaziv: " + naziv + "\tadresa: " + adresa + "\tidMes: " + idMes);
                                
			    }
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }      
    }

    private void otvoriRacun(int idkom, int idmes, int dozvminus) {
        String URLAddress = "http://localhost:8080/server/api/racuni";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("idKom=");
                queryParam.append(idkom);
                queryParam.append("&");
                queryParam.append("idMes=");
                queryParam.append(idmes);
                queryParam.append("&");
                queryParam.append("dozvMinus=");
                queryParam.append(dozvminus);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void zatvoriRacun(int idrac) {
        String URLAddress = "http://localhost:8080/server/api/racuni/zatvori";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("idRac=");
                queryParam.append(idrac);
                      
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void dohvRacuneKomitenta(int idkom) {
        String URLAddress = "http://localhost:8080/server/api/racuni/" + idkom;
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));

			    Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("racun");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idRac = getCharacterDataFromElement((Element) element.getElementsByTagName("idRac").item(0));
                                String komitent = getCharacterDataFromElement((Element) element.getElementsByTagName("komitent").item(0));
                                String status = getCharacterDataFromElement((Element) element.getElementsByTagName("status").item(0));
                                String stanje = getCharacterDataFromElement((Element) element.getElementsByTagName("stanje").item(0));
                                String dozvMinus = getCharacterDataFromElement((Element) element.getElementsByTagName("dozvMinus").item(0));
                                String mesto = getCharacterDataFromElement((Element) element.getElementsByTagName("mesto").item(0));
                                String brTransakcija = getCharacterDataFromElement((Element) element.getElementsByTagName("brTransakcija").item(0));
                           
                                System.out.println("idRac: " + idRac + "\tidKom: " + komitent + "\tstatus: " + intToChar(status) + "\tstanje: " + stanje +
                                                   "\tdozvMinus: " + dozvMinus + "\tidMes: " + mesto + "\tbrTransakcija: " + brTransakcija);
                                
			    }
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }      
    }

    private void dohvTransakcijeRacuna(int idrac) {
        String URLAddress = "http://localhost:8080/server/api/transakcije/" + idrac;
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));

			    Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("transakcija");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idRac = getCharacterDataFromElement((Element) element.getElementsByTagName("idRac").item(0));
                                String rb = getCharacterDataFromElement((Element) element.getElementsByTagName("rb").item(0));
                                String vrsta = getCharacterDataFromElement((Element) element.getElementsByTagName("vrsta").item(0));
                                String iznos = getCharacterDataFromElement((Element) element.getElementsByTagName("iznos").item(0));
                                String svrha = getCharacterDataFromElement((Element) element.getElementsByTagName("svrha").item(0));
                                String datum = getCharacterDataFromElement((Element) element.getElementsByTagName("datum").item(0));
                           
                                System.out.println("idRac: " + idRac + "\trb: " + rb + "\tvrsta: " + intToChar(vrsta) + "\tiznos: " + iznos +
                                                   "\tsvrha: " + svrha + "\tdatum: " + datum);
                                
			    }
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void transferNovca(int idracsa, int idracna, int iznos, String svrha) {
        String URLAddress = "http://localhost:8080/server/api/transakcije/prenos";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("idRacSa=");
                queryParam.append(idracsa);
                queryParam.append("&");
                queryParam.append("idRacNa=");
                queryParam.append(idracna);
                queryParam.append("&");
                queryParam.append("iznos=");
                queryParam.append(iznos);
                queryParam.append("&");
                queryParam.append("svrha=");
                queryParam.append(svrha);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void uplataIsplata(int idrac, int iznos, int idfil, String svrha) {
        String URLAddress = "http://localhost:8080/server/api/transakcije";
	String inputString = null;
	int responseCode = 0;
     
	try {
	URL url = new URL(URLAddress); 
            try {
                HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
                myHttpConnection.setRequestMethod("POST");
                myHttpConnection.setDoOutput(true);
                       
                StringBuffer queryParam = new StringBuffer();
               
                queryParam.append("idRac=");
                queryParam.append(idrac);
                queryParam.append("&");
                queryParam.append("idFil=");
                queryParam.append(idfil);
                queryParam.append("&");
                queryParam.append("iznos=");
                queryParam.append(iznos);
                queryParam.append("&");
                queryParam.append("svrha=");
                queryParam.append(svrha);
                
                // Output the results (remove when using GET)
                OutputStream output = myHttpConnection.getOutputStream();
                output.write(queryParam.toString().getBytes());
                output.flush();
                
                // get the response-code from the response 
                responseCode = myHttpConnection.getResponseCode();
         
                // print out URL details
                System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "POST", responseCode);
         
                System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) 
                    System.out.println(inputString);
                in.close();   
                System.out.println("----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }
    }

    private void dohvPodatke(Boolean razlika) {
        String URLAddress = "http://localhost:8080/server/api/podaci" + (razlika ? "/razlika" : "");
	String inputString = null;
	int responseCode = 0;
     
	try {
            URL url = new URL(URLAddress); 
            try {
		HttpURLConnection myHttpConnection = (HttpURLConnection) url.openConnection();
         
		myHttpConnection.setRequestMethod("GET");
		
                responseCode = myHttpConnection.getResponseCode();
         
		// print out URL details
		System.out.format("Connecting to %s\nConnection Method: '%s'\nResponse Code is: %d\n", URLAddress, "GET", responseCode);
         
		System.out.println("----------------------[ RESPONSE ]------------------------");
                        
                // open the contents of the URL as an inputStream and print to stdout
                BufferedReader in = new BufferedReader(new InputStreamReader(myHttpConnection.getInputStream()));
                while ((inputString = in.readLine()) != null) {
			try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db = dbf.newDocumentBuilder();
			    InputSource is = new InputSource();
			    is.setCharacterStream(new StringReader(inputString));
                            
                            ////////////////
                            
                            System.out.println("\t - MESTO -");
                            Document doc = db.parse(is);
			    NodeList nodes = doc.getElementsByTagName("mesto");

                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);

                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(0));
                                String postanskiBroj = getCharacterDataFromElement((Element) element.getElementsByTagName("postanskiBroj").item(0));
			
                                System.out.println("idMes: " + idMes + "\tnaziv: " + naziv + "\tpostanskiBroj: " + postanskiBroj);
                                
			    }
                            
                            ////////////////

                            System.out.println("\t - FILIJALA -");
			    nodes = doc.getElementsByTagName("filijala");

                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idFil = getCharacterDataFromElement((Element) element.getElementsByTagName("idFil").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(1));
                                String adresa = getCharacterDataFromElement((Element) element.getElementsByTagName("adresa").item(0));
                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0), "idMes");
                           
                                System.out.println("idFil: " + idFil + "\tnaziv: " + naziv + "\tadresa: " + adresa + "\tidMes: " + idMes);
                                
			    }
                            
                            //////////////////
                            
                            System.out.println("\t - KOMITENT -");
			    nodes = doc.getElementsByTagName("komitent");

		            // iterate mesto elements
                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idKom = getCharacterDataFromElement((Element) element.getElementsByTagName("idKom").item(0));
                                String naziv = getCharacterDataFromElement((Element) element.getElementsByTagName("naziv").item(1));
                                String adresa = getCharacterDataFromElement((Element) element.getElementsByTagName("adresa").item(0));
                                String idMes = getCharacterDataFromElement((Element) element.getElementsByTagName("idMes").item(0), "idMes");
                           
                                System.out.println("idKom: " + idKom + "\tnaziv: " + naziv + "\tadresa: " + adresa + "\tidMes: " + idMes);
                                
			    }
                            
                            ///////////////////
                            
                            System.out.println("\t - RACUN -");
			    nodes = doc.getElementsByTagName("racun");

                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idRac = getCharacterDataFromElement((Element) element.getElementsByTagName("idRac").item(0));
                                String komitent = getCharacterDataFromElement((Element) element.getElementsByTagName("komitent").item(0));
                                String status = getCharacterDataFromElement((Element) element.getElementsByTagName("status").item(0));
                                String stanje = getCharacterDataFromElement((Element) element.getElementsByTagName("stanje").item(0));
                                String dozvMinus = getCharacterDataFromElement((Element) element.getElementsByTagName("dozvMinus").item(0));
                                String mesto = getCharacterDataFromElement((Element) element.getElementsByTagName("mesto").item(0));
                                String brTransakcija = getCharacterDataFromElement((Element) element.getElementsByTagName("brTransakcija").item(0));
                           
                                System.out.println("idRac: " + idRac + "\tidKom: " + komitent + "\tstatus: " + intToChar(status) + "\tstanje: " + stanje +
                                                   "\tdozvMinus: " + dozvMinus + "\tidMes: " + mesto + "\tbrTransakcija: " + brTransakcija);
                                
			    }
                            
                            ///////////////////
                            
                            System.out.println("\t - TRANSAKCIJA -");
			    nodes = doc.getElementsByTagName("transakcija");

                            for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
                                
                                String idRac = getCharacterDataFromElement((Element) element.getElementsByTagName("idRac").item(0));
                                String rb = getCharacterDataFromElement((Element) element.getElementsByTagName("rb").item(0));
                                String vrsta = getCharacterDataFromElement((Element) element.getElementsByTagName("vrsta").item(0));
                                String iznos = getCharacterDataFromElement((Element) element.getElementsByTagName("iznos").item(0));
                                String svrha = getCharacterDataFromElement((Element) element.getElementsByTagName("svrha").item(0));
                                String datum = getCharacterDataFromElement((Element) element.getElementsByTagName("datum").item(0));
                           
                                System.out.println("idRac: " + idRac + "\trb: " + rb + "\tvrsta: " + intToChar(vrsta) + "\tiznos: " + iznos +
                                                   "\tsvrha: " + svrha + "\tdatum: " + datum);
                                
			    }
                            
                            ///////
                            
			} catch (Exception e) { e.printStackTrace(); }   
		}
		in.close();   
		System.out.println("-----------------------------------------------------------");
            } catch (IOException e) { e.printStackTrace(); }
        } catch (MalformedURLException e) { e.printStackTrace(); }  
    }

    /*
    private void dohvRazlikuUPodacima() {
        
    }
    */

}
