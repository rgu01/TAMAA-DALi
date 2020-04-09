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

public enum EquipmentType implements TEnum {
  CAMERA_360(0),
  CAMERA_PHOTO(1),
  CAMERA_VIDEO(2),
  IR_CAMERA_PHOTO(3),
  IR_CAMERA_VIDEO(4),
  WIFI(5),
  COLLISION_AVOIDANCE(6);

  private final int value;

  private EquipmentType(int value) {
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
  public static EquipmentType findByValue(int value) { 
    switch (value) {
      case 0:
        return CAMERA_360;
      case 1:
        return CAMERA_PHOTO;
      case 2:
        return CAMERA_VIDEO;
      case 3:
        return IR_CAMERA_PHOTO;
      case 4:
        return IR_CAMERA_VIDEO;
      case 5:
        return WIFI;
      case 6:
        return COLLISION_AVOIDANCE;
      default:
        return null;
    }
  }
}
