package entity;

import java.util.ArrayList;
import java.util.List;

import org.esfinge.aom.model.rolemapper.metadata.annotations.Entity;
import org.esfinge.aom.model.rolemapper.metadata.annotations.EntityProperty;
import org.esfinge.aom.model.rolemapper.metadata.annotations.EntityType;
import org.esfinge.aom.model.rolemapper.metadata.annotations.FixedEntityProperty;
import org.esfinge.aom.model.rolemapper.metadata.annotations.RuleMethod;

@Entity
public class Sensor implements ISensor {

	@EntityType
	private SensorType SensorType;
		
	@FixedEntityProperty
	private String dataFabricacao;
	
	@FixedEntityProperty
	private Boolean restcontroller;
	
	@EntityProperty
	private List<SensorProperty> properties = new ArrayList<SensorProperty>();

	public void addProperties (SensorProperty property)
	{
		this.properties.add(property);
	}

	public String getDataFabricacao() {
		return dataFabricacao;
	}

	public List<SensorProperty> getProperties() {
		return properties;
	}

	public Boolean getRestcontroller() {
		return restcontroller;
	}
	
	public SensorType getSensorType() {
		return SensorType;
	}

	@RuleMethod
	public boolean isValid(){
		boolean ret = true;
		ISensor iSensor = SensorType.createSensor();
		List<SensorProperty> properties2 = iSensor.getProperties();
		for (SensorProperty SensorProperty : properties2) {
			if(SensorProperty.getValue() == null){
				return false;
			}
		}
		return ret;
	}

	public void removeProperties (SensorProperty property)
	{
		this.properties.remove(property);
	}

	public void setDataFabricacao(String dataFabricacao) {
		this.dataFabricacao = dataFabricacao;
	}

	public void setProperties(List<SensorProperty> properties) {
		this.properties = properties;
	}
	
	public void setRestcontroller(Boolean restcontroller) {
		this.restcontroller = restcontroller;
	}
	
	public void setSensorType(SensorType SensorType) {
		this.SensorType = SensorType;
	}
}
