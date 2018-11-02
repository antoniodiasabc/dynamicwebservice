package tarefa6;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esfinge.aom.api.model.IEntity;
import org.esfinge.aom.api.model.IEntityType;
import org.esfinge.aom.exceptions.EsfingeAOMException;
import org.esfinge.aom.model.dynamic.factory.AdapterFactory;
import org.esfinge.aom.model.impl.CalculaAnos;
import org.esfinge.aom.model.impl.GenericEntityType;
import org.esfinge.aom.model.impl.GenericPropertyType;
import org.esfinge.aom.model.impl.PeriodoConsumo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TesteProduto {

	private AdapterFactory af;
	private String fileName;

	@Before
	public void setUp() throws Exception {
		fileName = "JsonMapTest.json";
		//String fileName2 = "C:\\INPE\\gs-rest-service-master\\complete\\src\\test\\resources\\JsonMapTest.json";
		af = AdapterFactory.getInstance(fileName);
	}
	
	private static IEntityType tipoProduto;
	private IEntity produto; 
	
	@BeforeClass
	public static void criarTipoEntidade() throws EsfingeAOMException{
		tipoProduto = FabricaTiposProduto.getTipoEntidadeCobranca();
		tipoProduto.addOperation("periodoConsumo", new PeriodoConsumo("dataFabricacao"));
	}
		
	@Before
	public void criarProdutoCorreto() throws EsfingeAOMException{
		produto = tipoProduto.createNewEntity();
		produto.setProperty("nome", "Yogurt X");
		GregorianCalendar dataFabr = new GregorianCalendar();
		dataFabr.set(2017, 2, 23);
		produto.setProperty("dataFabricacao", dataFabr.getTime());	
		produto.setProperty("perecivel", Boolean.TRUE);
		produto.setProperty("validade", 90);
	}

	@Test
	public void validaPessoaBase() throws Exception{
		Object resultOperation = produto.executeOperation("periodoConsumo");
		Long days = (Long) resultOperation;
		Integer value = (Integer) produto.getProperty("validade").getValue();
		boolean result = days < value.intValue();
		System.out.println(" periodo consumo " + days + " validade " + value);
		assertTrue("produto deve ser preenchido corretamente", result);
	}
	

	@Test
	public void testDataFabricacao() {
		try {
			IEntityType tipoProduto = new GenericEntityType("Produto");

			// criando property types
			GenericPropertyType dataNascPropertyType = new GenericPropertyType("dataFabricacao", Date.class);
			dataNascPropertyType.setProperty("notempty", true);

			GenericPropertyType nomePropertyType = new GenericPropertyType("nome", String.class);
			nomePropertyType.setProperty("notempty", true);

			// adicionando property types no tipo de entidade
			tipoProduto.addPropertyType(dataNascPropertyType);
			tipoProduto.addPropertyType(nomePropertyType);
			tipoProduto.addOperation("anosFabricacao", new CalculaAnos("dataFabricacao"));

			IEntity produto = tipoProduto.createNewEntity();
			produto.setProperty("nome", "Notebook DELL");
			GregorianCalendar dataFabr = new GregorianCalendar();
			dataFabr.set(2014, 11, 23);
			produto.setProperty("dataFabricacao", dataFabr.getTime());

			Object personAdapter = af.generate(produto);
			Method declaredMethod = personAdapter.getClass().getDeclaredMethod("executeOperation", String.class,
					Object[].class);
			Object resultOperation = declaredMethod.invoke(personAdapter, "anosFabricacao", null);

			int years = (int) resultOperation;
			System.out.println(" rule object anosFabricacao retornou " + years);
			boolean result = years < 7;
			assertTrue("produto deve ser preenchido corretamente", result);

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
//
//	@Test
//	public void testAddPropertyType_rule() throws Exception 
//	{	
//		AccountType accountType = new AccountType();
//		AdapterEntityType entityType = AdapterEntityType.getAdapter(accountType);	
//		IEntity iEntity = entityType.createNewEntity();
//		Object executeOperation = iEntity.executeOperation("fixedRule");
//		System.out.println("resultado " + executeOperation);
//	}
}
