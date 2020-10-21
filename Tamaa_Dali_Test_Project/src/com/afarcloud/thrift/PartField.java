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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2020-10-13")
public class PartField implements org.apache.thrift.TBase<PartField, PartField._Fields>, java.io.Serializable, Cloneable, Comparable<PartField> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PartField");

  private static final org.apache.thrift.protocol.TField PARTFIELD_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("partfieldId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField ISO_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("isoId", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField BORDER_POINTS_FIELD_DESC = new org.apache.thrift.protocol.TField("borderPoints", org.apache.thrift.protocol.TType.STRUCT, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PartFieldStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PartFieldTupleSchemeFactory());
  }

  public int partfieldId; // required
  public String isoId; // optional
  public String name; // required
  public Region borderPoints; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PARTFIELD_ID((short)1, "partfieldId"),
    ISO_ID((short)2, "isoId"),
    NAME((short)3, "name"),
    BORDER_POINTS((short)4, "borderPoints");

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
        case 1: // PARTFIELD_ID
          return PARTFIELD_ID;
        case 2: // ISO_ID
          return ISO_ID;
        case 3: // NAME
          return NAME;
        case 4: // BORDER_POINTS
          return BORDER_POINTS;
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
  private static final int __PARTFIELDID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.ISO_ID};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PARTFIELD_ID, new org.apache.thrift.meta_data.FieldMetaData("partfieldId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ISO_ID, new org.apache.thrift.meta_data.FieldMetaData("isoId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.BORDER_POINTS, new org.apache.thrift.meta_data.FieldMetaData("borderPoints", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Region.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PartField.class, metaDataMap);
  }

  public PartField() {
  }

  public PartField(
    int partfieldId,
    String name,
    Region borderPoints)
  {
    this();
    this.partfieldId = partfieldId;
    setPartfieldIdIsSet(true);
    this.name = name;
    this.borderPoints = borderPoints;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PartField(PartField other) {
    __isset_bitfield = other.__isset_bitfield;
    this.partfieldId = other.partfieldId;
    if (other.isSetIsoId()) {
      this.isoId = other.isoId;
    }
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetBorderPoints()) {
      this.borderPoints = new Region(other.borderPoints);
    }
  }

  public PartField deepCopy() {
    return new PartField(this);
  }

  @Override
  public void clear() {
    setPartfieldIdIsSet(false);
    this.partfieldId = 0;
    this.isoId = null;
    this.name = null;
    this.borderPoints = null;
  }

  public int getPartfieldId() {
    return this.partfieldId;
  }

  public PartField setPartfieldId(int partfieldId) {
    this.partfieldId = partfieldId;
    setPartfieldIdIsSet(true);
    return this;
  }

  public void unsetPartfieldId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PARTFIELDID_ISSET_ID);
  }

  /** Returns true if field partfieldId is set (has been assigned a value) and false otherwise */
  public boolean isSetPartfieldId() {
    return EncodingUtils.testBit(__isset_bitfield, __PARTFIELDID_ISSET_ID);
  }

  public void setPartfieldIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PARTFIELDID_ISSET_ID, value);
  }

  public String getIsoId() {
    return this.isoId;
  }

  public PartField setIsoId(String isoId) {
    this.isoId = isoId;
    return this;
  }

  public void unsetIsoId() {
    this.isoId = null;
  }

  /** Returns true if field isoId is set (has been assigned a value) and false otherwise */
  public boolean isSetIsoId() {
    return this.isoId != null;
  }

  public void setIsoIdIsSet(boolean value) {
    if (!value) {
      this.isoId = null;
    }
  }

  public String getName() {
    return this.name;
  }

  public PartField setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public Region getBorderPoints() {
    return this.borderPoints;
  }

  public PartField setBorderPoints(Region borderPoints) {
    this.borderPoints = borderPoints;
    return this;
  }

  public void unsetBorderPoints() {
    this.borderPoints = null;
  }

  /** Returns true if field borderPoints is set (has been assigned a value) and false otherwise */
  public boolean isSetBorderPoints() {
    return this.borderPoints != null;
  }

  public void setBorderPointsIsSet(boolean value) {
    if (!value) {
      this.borderPoints = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PARTFIELD_ID:
      if (value == null) {
        unsetPartfieldId();
      } else {
        setPartfieldId((Integer)value);
      }
      break;

    case ISO_ID:
      if (value == null) {
        unsetIsoId();
      } else {
        setIsoId((String)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case BORDER_POINTS:
      if (value == null) {
        unsetBorderPoints();
      } else {
        setBorderPoints((Region)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PARTFIELD_ID:
      return Integer.valueOf(getPartfieldId());

    case ISO_ID:
      return getIsoId();

    case NAME:
      return getName();

    case BORDER_POINTS:
      return getBorderPoints();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PARTFIELD_ID:
      return isSetPartfieldId();
    case ISO_ID:
      return isSetIsoId();
    case NAME:
      return isSetName();
    case BORDER_POINTS:
      return isSetBorderPoints();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PartField)
      return this.equals((PartField)that);
    return false;
  }

  public boolean equals(PartField that) {
    if (that == null)
      return false;

    boolean this_present_partfieldId = true;
    boolean that_present_partfieldId = true;
    if (this_present_partfieldId || that_present_partfieldId) {
      if (!(this_present_partfieldId && that_present_partfieldId))
        return false;
      if (this.partfieldId != that.partfieldId)
        return false;
    }

    boolean this_present_isoId = true && this.isSetIsoId();
    boolean that_present_isoId = true && that.isSetIsoId();
    if (this_present_isoId || that_present_isoId) {
      if (!(this_present_isoId && that_present_isoId))
        return false;
      if (!this.isoId.equals(that.isoId))
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_borderPoints = true && this.isSetBorderPoints();
    boolean that_present_borderPoints = true && that.isSetBorderPoints();
    if (this_present_borderPoints || that_present_borderPoints) {
      if (!(this_present_borderPoints && that_present_borderPoints))
        return false;
      if (!this.borderPoints.equals(that.borderPoints))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_partfieldId = true;
    list.add(present_partfieldId);
    if (present_partfieldId)
      list.add(partfieldId);

    boolean present_isoId = true && (isSetIsoId());
    list.add(present_isoId);
    if (present_isoId)
      list.add(isoId);

    boolean present_name = true && (isSetName());
    list.add(present_name);
    if (present_name)
      list.add(name);

    boolean present_borderPoints = true && (isSetBorderPoints());
    list.add(present_borderPoints);
    if (present_borderPoints)
      list.add(borderPoints);

    return list.hashCode();
  }

  @Override
  public int compareTo(PartField other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPartfieldId()).compareTo(other.isSetPartfieldId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPartfieldId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.partfieldId, other.partfieldId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIsoId()).compareTo(other.isSetIsoId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIsoId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.isoId, other.isoId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetBorderPoints()).compareTo(other.isSetBorderPoints());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBorderPoints()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.borderPoints, other.borderPoints);
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
    StringBuilder sb = new StringBuilder("PartField(");
    boolean first = true;

    sb.append("partfieldId:");
    sb.append(this.partfieldId);
    first = false;
    if (isSetIsoId()) {
      if (!first) sb.append(", ");
      sb.append("isoId:");
      if (this.isoId == null) {
        sb.append("null");
      } else {
        sb.append(this.isoId);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("borderPoints:");
    if (this.borderPoints == null) {
      sb.append("null");
    } else {
      sb.append(this.borderPoints);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (borderPoints != null) {
      borderPoints.validate();
    }
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

  private static class PartFieldStandardSchemeFactory implements SchemeFactory {
    public PartFieldStandardScheme getScheme() {
      return new PartFieldStandardScheme();
    }
  }

  private static class PartFieldStandardScheme extends StandardScheme<PartField> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PartField struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PARTFIELD_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.partfieldId = iprot.readI32();
              struct.setPartfieldIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ISO_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.isoId = iprot.readString();
              struct.setIsoIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // BORDER_POINTS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.borderPoints = new Region();
              struct.borderPoints.read(iprot);
              struct.setBorderPointsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PartField struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(PARTFIELD_ID_FIELD_DESC);
      oprot.writeI32(struct.partfieldId);
      oprot.writeFieldEnd();
      if (struct.isoId != null) {
        if (struct.isSetIsoId()) {
          oprot.writeFieldBegin(ISO_ID_FIELD_DESC);
          oprot.writeString(struct.isoId);
          oprot.writeFieldEnd();
        }
      }
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.borderPoints != null) {
        oprot.writeFieldBegin(BORDER_POINTS_FIELD_DESC);
        struct.borderPoints.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PartFieldTupleSchemeFactory implements SchemeFactory {
    public PartFieldTupleScheme getScheme() {
      return new PartFieldTupleScheme();
    }
  }

  private static class PartFieldTupleScheme extends TupleScheme<PartField> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PartField struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetPartfieldId()) {
        optionals.set(0);
      }
      if (struct.isSetIsoId()) {
        optionals.set(1);
      }
      if (struct.isSetName()) {
        optionals.set(2);
      }
      if (struct.isSetBorderPoints()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetPartfieldId()) {
        oprot.writeI32(struct.partfieldId);
      }
      if (struct.isSetIsoId()) {
        oprot.writeString(struct.isoId);
      }
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetBorderPoints()) {
        struct.borderPoints.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PartField struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.partfieldId = iprot.readI32();
        struct.setPartfieldIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.isoId = iprot.readString();
        struct.setIsoIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(3)) {
        struct.borderPoints = new Region();
        struct.borderPoints.read(iprot);
        struct.setBorderPointsIsSet(true);
      }
    }
  }

}

