package pojos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="healthprofile")
@XmlType(propOrder = { "lastUpdate", "weight", "height", "BMI" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HealthProfile {
	private Date lastUpdate;
	private double weight; // in kg
	private double height; // in m

	public HealthProfile(double weight, double height) {
		this.weight = weight;
		this.height = height;
		this.lastUpdate = new Date();
	}
	
	public HealthProfile(double weight, double height, Date lastUpdate) {
		this.weight = weight;
		this.height = height;
		this.lastUpdate = lastUpdate;
	}

	public HealthProfile() {
		this.weight = 85.5;
		this.height = 1.72;
		this.lastUpdate = new Date();
	}

	public Date getLsatUpdate() {
		return lastUpdate;
	}

	public void setLsatUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	public String toString() {
		return "Height="+height+", Weight="+weight;
	}

	@XmlElement(name="bmi")
	public double getBMI() {
		return this.weight/(Math.pow(this.height, 2));
	}
}
