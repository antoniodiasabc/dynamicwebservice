package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esfinge.aom.model.rolemapper.metadata.annotations.CreateEntityMethod;
import org.esfinge.aom.model.rolemapper.metadata.annotations.EntityType;
import org.esfinge.aom.model.rolemapper.metadata.annotations.Metadata;
import org.esfinge.aom.model.rolemapper.metadata.annotations.PropertyType;
import org.esfinge.aom.model.rolemapper.metadata.annotations.RuleMethod;

@EntityType
public class SensorType {

	@PropertyType
	private Set<SensorPropertyType> propertyTypes = new HashSet<SensorPropertyType>();
	
	@Metadata
	private List<MetadatasAccountType> metadatas = new ArrayList<MetadatasAccountType>();
	
	@CreateEntityMethod
	public ISensor createSensor() {
		ISensor sensor = null;
		sensor = (ISensor) new Sensor();
		sensor.setSensorType(this);
		return sensor;
	}

	@RuleMethod
	public boolean isValid() {
		boolean ret = true;
		ISensor iSensor = createSensor();
		List<SensorProperty> properties2 = iSensor.getProperties();
		for (SensorProperty SensorProperty : properties2) {
			if (SensorProperty.getValue() == null) {
				return false;
			}
		}
		return ret;
	}

	public Set<SensorPropertyType> getPropertyTypes() {
		return propertyTypes;
	}

	public void setPropertyTypes(Set<SensorPropertyType> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	public void addPropertyTypes(SensorPropertyType propertyType) {
		propertyTypes.add(propertyType);
	}

	public void add2(SensorPropertyType propertyType) {
		propertyTypes.add(propertyType);
	}

	public void removePropertyTypes(SensorPropertyType propertyType) {
		propertyTypes.remove(propertyType);
	}
	
	public List<MetadatasAccountType> getMetadatas() {
		return metadatas;
	}

	public void setMetadatas(List<MetadatasAccountType> metadatas) {
		this.metadatas = metadatas;
	}
	
}
