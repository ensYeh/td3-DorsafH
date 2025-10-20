package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

    private Path fichierDNS;
    private Dns dns;

    @Before
    public void setUp() throws IOException {
        // Réinitialiser dns.txt à un état connu
        fichierDNS = Paths.get("dns.txt");
        List<String> lignesInitiales = List.of(
                "www.uvsq.fr 193.51.31.90",
                "poste.uvsq.fr 193.51.31.154",
                "ecampus.uvsq.fr 193.51.25.12"
        );
        Files.write(fichierDNS, lignesInitiales, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        dns = new Dns(fichierDNS);
    }

    // Test getItem(NomMachine)
    @Test
    public void testGetItemNomMachine() {
        NomMachine nom = new NomMachine("www.uvsq.fr");
        DnsItem item = dns.getItem(nom);
        assertNotNull(item);
        assertEquals("193.51.31.90", item.getAdresseIP().getIp());
    }

    // Test getItem(AdresseIP)
    @Test
    public void testGetItemAdresseIP() {
        AdresseIP ip = new AdresseIP("193.51.31.154");
        DnsItem item = dns.getItem(ip);
        assertNotNull(item);
        assertEquals("poste.uvsq.fr", item.getNomMachine().getNomQualifie());
    }

    // Test getItems(String domaine)
    @Test
    public void testGetItemsDomaine() {
        List<DnsItem> items = dns.getItems("uvsq.fr");
        assertEquals(3, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getNomMachine().getNomQualifie().equals("www.uvsq.fr")));
    }

    // Test addItem() normal
    @Test
    public void testAddItem() throws IOException {
        AdresseIP ip = new AdresseIP("193.51.25.24");
        NomMachine nom = new NomMachine("etudiante.uvsq.fr");
        dns.addItem(ip, nom);

        DnsItem item = dns.getItem(nom);
        assertNotNull(item);
        assertEquals(ip, item.getAdresseIP());
    }

    // Test addItem() doublon nom
    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateNom() throws IOException {
        AdresseIP ip = new AdresseIP("193.51.25.90");
        NomMachine nom = new NomMachine("www.uvsq.fr"); // déjà présent
        dns.addItem(ip, nom);
    }

    // Test addItem() doublon IP
    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateIP() throws IOException {
        AdresseIP ip = new AdresseIP("193.51.31.90"); // déjà présent
        NomMachine nom = new NomMachine("nouvelle.uvsq.fr");
        dns.addItem(ip, nom);
    }

    // Test rechercherIP(String nom)
    @Test
    public void testRechercherIP() {
        DnsItem item = dns.getItem(new NomMachine("poste.uvsq.fr"));
AdresseIP ip = item != null ? item.getAdresseIP() : null;
        assertNotNull(ip);
        assertEquals("193.51.31.154", ip.getIp());
    }

    // Test rechercherNom(String ip)
    @Test
    public void testRechercherNom() {
       DnsItem item = dns.getItem(new AdresseIP("193.51.25.12"));
NomMachine nom = item != null ? item.getNomMachine() : null;
        assertNotNull(nom);
        assertEquals("ecampus.uvsq.fr", nom.getNomQualifie());
    }
}
