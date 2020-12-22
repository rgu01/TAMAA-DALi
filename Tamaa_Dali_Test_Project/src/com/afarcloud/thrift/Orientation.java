/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.afarcloud.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2020-11-24")
public class Orientation implements org.apache.thrift.TBase<Orientation, Orientation._Fields>, java.io.Serializable, Cloneable, Comparable<Orientation> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Orientation");

  private static final org.apache.thrift.protocol.TField ROLL_FIELD_DESC = new org.apache.thrift.protocol.TField("roll", org.apache.thrift.protocol.TType.DOUBLE, (short)1);
  private static final org.apache.thrift.protocol.TField PITCH_FIELD_DESC = new org.apache.thrift.protocol.TField("pitch", org.apache.thrift.protocol.TType.DOUBLE, (short)2);
  private static final org.apache.thrift.protocol.TField YAW_FIELD_DESC = new org.apache.thrift.protocol.TField("yaw", org.apache.thrift.protocol.TType.DOUBLE, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new OrientationStandardSchemeFactory());
    schemes.put(TupleScheme.class, new OrientationTupleSchemeFactory());
  }

  public double roll; // required
  public double pitch; // required
  public double yaw; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ROLL((short)1, "roll"),
    PITCH((short)2, "pitch"),
    YAW((short)3, "yaw");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ROLL
          return ROLL;
        case 2: // PITCH
          return PITCH;
        case 3: // YAW
          return YAW;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ROLL_ISSET_ID = 0;
  private static final int __PITCH_ISSET_ID = 1;
  private static final int __YAW_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ROLL, new org.apache.thrift.meta_data.FieldMetaData("roll", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.PITCH, new org.apache.thrift.meta_data.FieldMetaData("pitch", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.YAW, new org.apache.thrift.meta_data.FieldMetaData("yaw", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Orientation.class, metaDataMap);
  }

  public Orientation() {
  }

  public Orientation(
    double roll,
    double pitch,
    double yaw)
  {
    this();
    this.roll = roll;
    setRollIsSet(true);
    this.pitch = pitch;
    setPitchIsSet(true);
    this.yaw = yaw;
    setYawIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Orientation(Orientation other) {
    __isset_bitfield = other.__isset_bitfield;
    this.roll = other.roll;
    this.pitch = other.pitch;
    this.yaw = other.yaw;
  }

  public Orientation deepCopy() {
    return new Orientation(this);
  }

  @Override
  public void clear() {
    setRollIsSet(false);
    this.roll = 0.0;
    setPitchIsSet(false);
    this.pitch = 0.0;
    setYawIsSet(false);
    this.yaw = 0.0;
  }

  public double getRoll() {
    return this.roll;
  }

  public Orientation setRoll(double roll) {
    this.roll = roll;
    setRollIsSet(true);
    return this;
  }

  public void unsetRoll() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ROLL_ISSET_ID);
  }

  /** Returns true if field roll is set (has been assigned a value) and false otherwise */
  public boolean isSetRoll() {
    return EncodingUtils.testBit(__isset_bitfield, __ROLL_ISSET_ID);
  }

  public void setRollIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ROLL_ISSET_ID, value);
  }

  public double getPitch() {
    return this.pitch;
  }

  public Orientation setPitch(double pitch) {
    this.pitch = pitch;
    setPitchIsSet(true);
    return this;
  }

  public void unsetPitch() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PITCH_ISSET_ID);
  }

  /** Returns true if field pitch is set (has been assigned a value) and false otherwise */
  public boolean isSetPitch() {
    return EncodingUtils.testBit(__isset_bitfield, __PITCH_ISSET_ID);
  }

  public void setPitchIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PITCH_ISSET_ID, value);
  }

  public double getYaw() {
    return this.yaw;
  }

  public Orientation setYaw(double yaw) {
    this.yaw = yaw;
    setYawIsSet(true);
    return this;
  }

  public void unsetYaw() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __YAW_ISSET_ID);
  }

  /** Returns true if field yaw is set (has been assigned a value) and false otherwise */
  public boolean isSetYaw() {
    return EncodingUtils.testBit(__isset_bitfield, __YAW_ISSET_ID);
  }

  public void setYawIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __YAW_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ROLL:
      if (value == null) {
        unsetRoll();
      } else {
        setRoll((Double)value);
      }
      break;

    case PITCH:
      if (value == null) {
        unsetPitch();
      } else {
        setPitch((Double)value);
      }
      break;

    case YAW:
      if (value == null) {
        unsetYaw();
      } else {
        setYaw((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ROLL:
      return Double.valueOf(getRoll());

    case PITCH:
      return Double.valueOf(getPitch());

    case YAW:
      return Double.valueOf(getYaw());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ROLL:
      return isSetRoll();
    case PITCH:
      return isSetPitch();
    case YAW:
      return isSetYaw();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Orientation)
      return this.equals((Orientation)that);
    return false;
  }

  public boolean equals(Orientation that) {
    if (that == null)
      return false;

    boolean this_present_roll = true;
    boolean that_present_roll = true;
    if (this_present_roll || that_present_roll) {
      if (!(this_present_roll && that_present_roll))
        return false;
      if (this.roll != that.roll)
        return false;
    }

    boolean this_present_pitch = true;
    boolean that_present_pitch = true;
    if (this_present_pitch || that_present_pitch) {
      if (!(this_present_pitch && that_present_pitch))
        return false;
      if (this.pitch != that.pitch)
        return false;
    }

    boolean this_present_yaw = true;
    boolean that_present_yaw = true;
    if (this_present_yaw || that_present_yaw) {
      if (!(this_present_yaw && that_present_yaw))
        return false;
      if (this.yaw != that.yaw)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_roll = true;
    list.add(present_roll);
    if (present_roll)
      list.add(roll);

    boolean present_pitch = true;
    list.add(present_pitch);
    if (present_pitch)
      list.add(pitch);

    boolean present_yaw = true;
    list.add(present_yaw);
    if (present_yaw)
      list.add(yaw);

    return list.hashCode();
  }

  @Override
  public int compareTo(Orientation other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetRoll()).compareTo(other.isSetRoll());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRoll()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.roll, other.roll);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPitch()).compareTo(other.isSetPitch());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPitch()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.pitch, other.pitch);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetYaw()).compareTo(other.isSetYaw());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetYaw()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.yaw, other.yaw);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Orientation(");
    boolean first = true;

    sb.append("roll:");
    sb.append(this.roll);
    first = false;
    if (!first) sb.append(", ");
    sb.append("pitch:");
    sb.append(this.pitch);
    first = false;
    if (!first) sb.append(", ");
    sb.append("yaw:");
    sb.append(this.yaw);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class OrientationStandardSchemeFactory implements SchemeFactory {
    public OrientationStandardScheme getScheme() {
      return new OrientationStandardScheme();
    }
  }

  private static class OrientationStandardScheme extends StandardScheme<Orientation> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Orientation struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ROLL
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.roll = iprot.readDouble();
              struct.setRollIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PITCH
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.pitch = iprot.readDouble();
              struct.setPitchIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // YAW
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.yaw = iprot.readDouble();
              struct.setYawIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Orientation struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ROLL_FIELD_DESC);
      oprot.writeDouble(struct.roll);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PITCH_FIELD_DESC);
      oprot.writeDouble(struct.pitch);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(YAW_FIELD_DESC);
      oprot.writeDouble(struct.yaw);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class OrientationTupleSchemeFactory implements SchemeFactory {
    public OrientationTupleScheme getScheme() {
      return new OrientationTupleScheme();
    }
  }

  private static class OrientationTupleScheme extends TupleScheme<Orientation> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Orientation struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetRoll()) {
        optionals.set(0);
      }
      if (struct.isSetPitch()) {
        optionals.set(1);
      }
      if (struct.isSetYaw()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetRoll()) {
        oprot.writeDouble(struct.roll);
      }
      if (struct.isSetPitch()) {
        oprot.writeDouble(struct.pitch);
      }
      if (struct.isSetYaw()) {
        oprot.writeDouble(struct.yaw);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Orientation struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.roll = iprot.readDouble();
        struct.setRollIsSet(true);
      }
      if (incoming.get(1)) {
        struct.pitch = iprot.readDouble();
        struct.setPitchIsSet(true);
      }
      if (incoming.get(2)) {
        struct.yaw = iprot.readDouble();
        struct.setYawIsSet(true);
      }
    }
  }

}

