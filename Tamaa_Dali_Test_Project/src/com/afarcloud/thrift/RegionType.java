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

public enum RegionType implements org.apache.thrift.TEnum {
  UNDEFINED(0),
  NAVIGATION(1),
  FORBIDDEN(2),
  PREFERRED(3),
  LESS_PREFERRED(4),
  HEAT_REGION(5);

  private final int value;

  private RegionType(int value) {
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
  public static RegionType findByValue(int value) { 
    switch (value) {
      case 0:
        return UNDEFINED;
      case 1:
        return NAVIGATION;
      case 2:
        return FORBIDDEN;
      case 3:
        return PREFERRED;
      case 4:
        return LESS_PREFERRED;
      case 5:
        return HEAT_REGION;
      default:
        return null;
    }
  }
}
