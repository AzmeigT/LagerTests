package buchhandlung.lager;

import static org.salespointframework.core.Currencies.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Streamable;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import com.sun.xml.bind.v2.schemagen.xmlschema.Any;

import buchhandlung.AbstractIntegrationTests;
import buchhandlung.katalog.Artikel;
import buchhandlung.katalog.Artikel.ArtikelType;
import buchhandlung.katalog.Buch;
import buchhandlung.katalog.BuchhandlungKatalog;
import buchhandlung.katalog.CdDvd;
import buchhandlung.lager.LagerController;

@SpringBootTest
@AutoConfigureMockMvc
public class LagerControllerIntegrationTest extends AbstractIntegrationTests {
	
	@Autowired
	MockMvc mvc;
	
	@Autowired
	BuchhandlungKatalog katalog;
	
	@Autowired
	UniqueInventory<UniqueInventoryItem> lager;
	
	@Autowired
	LagerController controller;
	
	@Test
	void add_book_test() throws Exception {
		Buch mockedBuch = new Buch("Titel", Money.of(100, EURO), "Horror", ArtikelType.BUCH, "Inhalt", "12345678", "Autor", "lac.jpg", "Verlag");
		
		long count_before = lager.count();
		String direction = controller.add_book("Titel", 100.0, "Horror", "Inhalt", "12345678", "Autor", "lac.jpg", "Verlag");
		/*mvc.perform(get("/add_book").param("prod_name", "Titel").param("prod_preis", "100").param("prod_genre", "Horror") //
				.param("prod_inhalt", "Inhalt").param("prod_isbn", "12345678").param("prod_autor", "Autor").param("prod_abbildung", "lac.jpg") //
				.param("prod_verlag", "Verlag")).andExpect(redirectedUrl("/lager"));*/
		long count_after = lager.count();
		assert count_after == count_before + 1 : "\nNumber of items in lager didnt increase!\n";
		assert direction.contentEquals("redirect:/lager") : "\nWrong redirect value: " + direction + "\n";		
	}
	
	@Test
	void add_book_admin_test() throws Exception {
		
		long count_before = lager.count();
		String direction = controller.add_book_admin("Titel", 100.0, "Horror", "Inhalt", "12345678", "Autor", "lac.jpg", "Verlag");
		long count_after = lager.count();
		assert count_after == count_before + 1 : "\nNumber of items in lager didnt increase!\n";
		assert direction.contentEquals("redirect:/lager_admin") : "\nWrong redirect value: " + direction + "\n";	
	}
	
	@Test
	void add_film_test() throws Exception {
		
		long count_before = lager.count();
		String direction = controller.add_disc("Titel", 100.0, "Horror", "Inhalt", "Autor", "lac.jpg");
		long count_after = lager.count();
		assert count_after == count_before + 1 : "\nNumber of items in lager didnt increase!\n";
		assert direction.contentEquals("redirect:/lager") : "\nWrong redirect value: " + direction + "\n";		
	}
	
	@Test
	void add_film_admin_test() throws Exception {
		
		long count_before = lager.count();
		String direction = controller.add_disc_admin("Titel", 100.0, "Horror", "Inhalt", "Autor", "lac.jpg");
		long count_after = lager.count();
		assert count_after == count_before + 1 : "\nNumber of items in lager didnt increase!\n";
		assert direction.contentEquals("redirect:/lager_admin") : "\nWrong redirect value: " + direction + "\n";	
	}
	
	@Test
	void reorder_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			Quantity count_before = i.getQuantity();
			String direction = controller.reorder(5, i.getProduct().getId());
			Quantity count_after = i.getQuantity();
			assert count_after.equals(count_before.add(Quantity.of(5))): "\nItem quantity in lager didnt increase!\n";
			assert direction.contentEquals("redirect:/lager") : "\nWrong redirect value: " + direction + "\n";	
		}
	}
	
	@Test
	void reorder_admin_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			Quantity count_before = i.getQuantity();
			String direction = controller.reorder_admin(5, i.getProduct().getId());
			Quantity count_after = i.getQuantity();
			assert count_after.equals(count_before.add(Quantity.of(5))): "\nItem quantity in lager didnt increase!\n";
			assert direction.contentEquals("redirect:/lager_admin") : "\nWrong redirect value: " + direction + "\n";	
		}
	}
	
	@Test
	void delete_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			ProductIdentifier id = i.getProduct().getId();
			long count_before = lager.count();
			String direction = controller.delete(id);
			long count_after = lager.count();
			assert count_after == count_before - 1 : "\nNumber of items in lager didnt decrease!\n";
			assert direction.contentEquals("redirect:/lager") : "\nWrong redirect value: " + direction + "\n";	
		}	
	}
	
	@Test
	void delete_admin_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			ProductIdentifier id = i.getProduct().getId();
			long count_before = lager.count();
			String direction = controller.delete_admin(id);
			long count_after = lager.count();
			assert count_after == count_before - 1 : "\nNumber of items in lager didnt decrease!\n";
			assert direction.contentEquals("redirect:/lager_admin") : "\nWrong redirect value: " + direction + "\n";	
		}	
	}
	
	@Test
	void lager_produktdetails_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			ProductIdentifier id = i.getProduct().getId();
			if (i.getProduct() instanceof Buch) {
				mvc.perform(get("/lager/produktdetails").param("prod_id", id.toString())).andExpect(view().name("lager_details_book")).andExpect(status().isOk());
			} else if (i.getProduct() instanceof CdDvd) {
				mvc.perform(get("/lager/produktdetails").param("prod_id", id.toString())).andExpect(view().name("lager_details_cddvd")).andExpect(status().isOk());
			}
			
		}	
	}
	
	@Test
	void lager_produktdetails_admin_test() throws Exception {
		
		for (UniqueInventoryItem i: lager.findAll()) {
			ProductIdentifier id = i.getProduct().getId();
			if (i.getProduct() instanceof Buch) {
				mvc.perform(get("/lager/produktdetails_admin").param("prod_id", id.toString())).andExpect(view().name("lager_details_book_admin")).andExpect(status().isOk());
			} else if (i.getProduct() instanceof CdDvd) {
				mvc.perform(get("/lager/produktdetails_admin").param("prod_id", id.toString())).andExpect(view().name("lager_details_cddvd_admin")).andExpect(status().isOk());
			}
			
		}	
	}

}
