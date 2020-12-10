package buchhandlung.lager;

import static org.salespointframework.core.Currencies.*;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.salespointframework.Salespoint;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Streamable;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import com.sun.xml.bind.v2.schemagen.xmlschema.Any;

import buchhandlung.katalog.Artikel;
import buchhandlung.katalog.Artikel.ArtikelType;
import buchhandlung.katalog.Buch;
import buchhandlung.katalog.BuchhandlungKatalog;
import buchhandlung.lager.LagerController;

@SpringBootTest
@AutoConfigureMockMvc
class LagerControllerUnitTest {
	
	@Autowired
	MockMvc mvc;
	
	@Mock
	BuchhandlungKatalog katalog;
	
	@MockBean
	UniqueInventory<UniqueInventoryItem> lager;
	
	@Test
	void add_book_redirects_to_lager() throws Exception {
		/*Buch b = new Buch("Last Action Hero", Money.of(10, EURO), "Action/Comedy", ArtikelType.BUCH, "abc", "1234", "xyz", "lac.jpg", "ggg");
		when(katalog.save(b)).thenReturn(b);
		UniqueInventoryItem item = new UniqueInventoryItem(b,Quantity.of(0));
		when(lager.save(item)).thenReturn(item);
		ArrayList<Artikel> al = new ArrayList<Artikel>();
		al.add(b);
		Streamable<Artikel> ss = new PageImpl<Artikel>(al);
		System.out.println("BARTEK" + ss);
		when(katalog.findAll()).thenReturn(ss);*/
		
		mvc.perform(get("/add_book").param("prod_name", "Last Action Hero").param("prod_preis", "10").param("prod_genre", "Action/Comedy") //
									.param("prod_inhalt", "abc").param("prod_isbn", "1234").param("prod_autor", "xyz").param("prod_abbildung", "lac.jpg") //
									.param("prod_verlag", "ggg")).andExpect(redirectedUrl("/lager"));
	}
	
	@Test
	void add_book_admin_redirects_to_lager_admin() throws Exception {
		
		mvc.perform(get("/add_book_admin").param("prod_name", "Last Action Hero").param("prod_preis", "10").param("prod_genre", "Action/Comedy") //
				.param("prod_inhalt", "abc").param("prod_isbn", "1234").param("prod_autor", "xyz").param("prod_abbildung", "lac.jpg") //
				.param("prod_verlag", "ggg")).andExpect(redirectedUrl("/lager_admin"));
	}
	
	@Test
	void add_film_redirects_to_lager() throws Exception {
		
		mvc.perform(get("/add_film").param("prod_name", "Last Action Hero").param("prod_preis", "10").param("prod_genre", "Action/Comedy") //
				  .param("prod_inhalt", "abc").param("prod_autor", "xyz").param("prod_abbildung", "lac.jpg")).andExpect(redirectedUrl("/lager"));
	}
	
	@Test
	void add_film_admin_redirects_to_lager_admin() throws Exception {
		mvc.perform(get("/add_film_admin").param("prod_name", "Last Action Hero").param("prod_preis", "10").param("prod_genre", "Action/Comedy") //
										  .param("prod_inhalt", "abc").param("prod_autor", "xyz").param("prod_abbildung", "lac.jpg")).andExpect(redirectedUrl("/lager_admin"));
	}
	
	@Test
	void lager_reorder_redirects_to_lager() throws Exception {
		
		mvc.perform(get("/lager/reorder").param("prod_id", "1234").param("to_order", "10")).andExpect(redirectedUrl("/lager"));
	}
	
	@Test
	void lager_reorder_admin_redirects_to_lager_admin() throws Exception {
		
		mvc.perform(get("/lager/reorder_admin").param("prod_id", "1234").param("to_order", "10")).andExpect(redirectedUrl("/lager_admin"));
	}
	
	@Test
	void lager_delete_redirects_to_lager() throws Exception {
		
		mvc.perform(get("/lager/delete").param("prod_id", "1234")).andExpect(redirectedUrl("/lager"));
	}
	
	@Test
	void lager_delete_admin_redirects_to_lager_admin() throws Exception {
		
		mvc.perform(get("/lager/delete_admin").param("prod_id", "1234")).andExpect(redirectedUrl("/lager_admin"));
	}

	@Test
	void lager_shows_lager_page() throws Exception {
		mvc.perform(get("/lager")) //
				.andExpect(view().name("lager"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Warenansicht")))
				.andExpect(content().string(containsString("Ware registrieren")));		
	}
	
	@Test
	void lager_admin_shows_lager_admin_page() throws Exception {
		mvc.perform(get("/lager_admin")) //
				.andExpect(view().name("lager_admin"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Warenansicht")))
				.andExpect(content().string(containsString("Ware registrieren")));		
	}	
	

}
