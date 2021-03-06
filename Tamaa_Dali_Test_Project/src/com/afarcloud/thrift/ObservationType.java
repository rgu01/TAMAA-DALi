/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.afarcloud.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum ObservationType implements org.apache.thrift.TEnum {
  acceleration(0),
  air_flow(1),
  air_humidity(2),
  air_pressure(3),
  air_temperature(4),
  altitude(5),
  application_rate_mg_m2(6),
  application_rate_mm3_m2(7),
  battery(8),
  co2(9),
  condensed_work_state(10),
  crude_protein(11),
  d_value(12),
  dew_point_temperature(13),
  dry_matter(14),
  electrical_conductivity_bulk(15),
  electrical_conductivity_pores(16),
  engine_percentage_torque(17),
  engine_speed(18),
  engine_total_hours_of_operation(19),
  fibre(20),
  field_status(21),
  filter_fouling_level_F7(22),
  filter_fouling_level_G4(23),
  filter_fouling_level_H13(24),
  fuel_ratio(25),
  heat_index_temperature(26),
  hg_depth(27),
  hg_score(28),
  illuminance(29),
  instantaneous_fuel_consumption_per_area(30),
  instantaneous_fuel_consumption_per_time(31),
  irrigation(32),
  latitude(33),
  longitude(34),
  ndvi(35),
  object_detected(36),
  particulate_matter(37),
  pmr_in_1d(38),
  pmr_in_2d(39),
  pmr_in_3d(40),
  pmr_in_4d(41),
  pmr_in_5d(42),
  pos_x(43),
  pos_y(44),
  pos_z(45),
  powdery_mildew_risk(46),
  protein(47),
  rainfall(48),
  rainfall_gain(49),
  relative_dielectric_constant(50),
  signal_strength(51),
  soil_humidity(52),
  soil_matrix_potential(53),
  soil_temperature(54),
  solar_radiation(55),
  speed(56),
  status(57),
  temperature_teros12(58),
  temperature_teros21(59),
  total_fuel_used(60),
  total_volatile_organic_compound(61),
  vehicle_speed(62),
  volumetric_water_content_mineral_soil(63),
  water_consumption(64),
  wind_direction(65),
  wind_gain(66),
  wind_speed(67);

  private final int value;

  private ObservationType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static ObservationType findByValue(int value) { 
    switch (value) {
      case 0:
        return acceleration;
      case 1:
        return air_flow;
      case 2:
        return air_humidity;
      case 3:
        return air_pressure;
      case 4:
        return air_temperature;
      case 5:
        return altitude;
      case 6:
        return application_rate_mg_m2;
      case 7:
        return application_rate_mm3_m2;
      case 8:
        return battery;
      case 9:
        return co2;
      case 10:
        return condensed_work_state;
      case 11:
        return crude_protein;
      case 12:
        return d_value;
      case 13:
        return dew_point_temperature;
      case 14:
        return dry_matter;
      case 15:
        return electrical_conductivity_bulk;
      case 16:
        return electrical_conductivity_pores;
      case 17:
        return engine_percentage_torque;
      case 18:
        return engine_speed;
      case 19:
        return engine_total_hours_of_operation;
      case 20:
        return fibre;
      case 21:
        return field_status;
      case 22:
        return filter_fouling_level_F7;
      case 23:
        return filter_fouling_level_G4;
      case 24:
        return filter_fouling_level_H13;
      case 25:
        return fuel_ratio;
      case 26:
        return heat_index_temperature;
      case 27:
        return hg_depth;
      case 28:
        return hg_score;
      case 29:
        return illuminance;
      case 30:
        return instantaneous_fuel_consumption_per_area;
      case 31:
        return instantaneous_fuel_consumption_per_time;
      case 32:
        return irrigation;
      case 33:
        return latitude;
      case 34:
        return longitude;
      case 35:
        return ndvi;
      case 36:
        return object_detected;
      case 37:
        return particulate_matter;
      case 38:
        return pmr_in_1d;
      case 39:
        return pmr_in_2d;
      case 40:
        return pmr_in_3d;
      case 41:
        return pmr_in_4d;
      case 42:
        return pmr_in_5d;
      case 43:
        return pos_x;
      case 44:
        return pos_y;
      case 45:
        return pos_z;
      case 46:
        return powdery_mildew_risk;
      case 47:
        return protein;
      case 48:
        return rainfall;
      case 49:
        return rainfall_gain;
      case 50:
        return relative_dielectric_constant;
      case 51:
        return signal_strength;
      case 52:
        return soil_humidity;
      case 53:
        return soil_matrix_potential;
      case 54:
        return soil_temperature;
      case 55:
        return solar_radiation;
      case 56:
        return speed;
      case 57:
        return status;
      case 58:
        return temperature_teros12;
      case 59:
        return temperature_teros21;
      case 60:
        return total_fuel_used;
      case 61:
        return total_volatile_organic_compound;
      case 62:
        return vehicle_speed;
      case 63:
        return volumetric_water_content_mineral_soil;
      case 64:
        return water_consumption;
      case 65:
        return wind_direction;
      case 66:
        return wind_gain;
      case 67:
        return wind_speed;
      default:
        return null;
    }
  }
}
