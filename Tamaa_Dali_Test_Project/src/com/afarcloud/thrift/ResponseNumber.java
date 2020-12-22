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
public class ResponseNumber implements org.apache.thrift.TBase<ResponseNumber, ResponseNumber._Fields>, java.io.Serializable, Cloneable, Comparable<ResponseNumber> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResponseNumber");

  private static final org.apache.thrift.protocol.TField TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("Timestamp", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField OBSERVATION_FIELD_DESC = new org.apache.thrift.protocol.TField("Observation", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField UNITS_FIELD_DESC = new org.apache.thrift.protocol.TField("Units", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField RESULT_FIELD_DESC = new org.apache.thrift.protocol.TField("Result", org.apache.thrift.protocol.TType.DOUBLE, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ResponseNumberStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ResponseNumberTupleSchemeFactory());
  }

  public long Timestamp; // required
  /**
   * 
   * @see com.afarcloud.thrift.ObservationType
   */
  public com.afarcloud.thrift.ObservationType Observation; // required
  public String Units; // required
  public double Result; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TIMESTAMP((short)1, "Timestamp"),
    /**
     * 
     * @see com.afarcloud.thrift.ObservationType
     */
    OBSERVATION((short)2, "Observation"),
    UNITS((short)3, "Units"),
    RESULT((short)4, "Result");

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
        case 1: // TIMESTAMP
          return TIMESTAMP;
        case 2: // OBSERVATION
          return OBSERVATION;
        case 3: // UNITS
          return UNITS;
        case 4: // RESULT
          return RESULT;
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
  private static final int __TIMESTAMP_ISSET_ID = 0;
  private static final int __RESULT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("Timestamp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.OBSERVATION, new org.apache.thrift.meta_data.FieldMetaData("Observation", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, com.afarcloud.thrift.ObservationType.class)));
    tmpMap.put(_Fields.UNITS, new org.apache.thrift.meta_data.FieldMetaData("Units", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RESULT, new org.apache.thrift.meta_data.FieldMetaData("Result", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResponseNumber.class, metaDataMap);
  }

  public ResponseNumber() {
  }

  public ResponseNumber(
    long Timestamp,
    com.afarcloud.thrift.ObservationType Observation,
    String Units,
    double Result)
  {
    this();
    this.Timestamp = Timestamp;
    setTimestampIsSet(true);
    this.Observation = Observation;
    this.Units = Units;
    this.Result = Result;
    setResultIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResponseNumber(ResponseNumber other) {
    __isset_bitfield = other.__isset_bitfield;
    this.Timestamp = other.Timestamp;
    if (other.isSetObservation()) {
      this.Observation = other.Observation;
    }
    if (other.isSetUnits()) {
      this.Units = other.Units;
    }
    this.Result = other.Result;
  }

  public ResponseNumber deepCopy() {
    return new ResponseNumber(this);
  }

  @Override
  public void clear() {
    setTimestampIsSet(false);
    this.Timestamp = 0;
    this.Observation = null;
    this.Units = null;
    setResultIsSet(false);
    this.Result = 0.0;
  }

  public long getTimestamp() {
    return this.Timestamp;
  }

  public ResponseNumber setTimestamp(long Timestamp) {
    this.Timestamp = Timestamp;
    setTimestampIsSet(true);
    return this;
  }

  public void unsetTimestamp() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  /** Returns true if field Timestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetTimestamp() {
    return EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  public void setTimestampIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
  }

  /**
   * 
   * @see com.afarcloud.thrift.ObservationType
   */
  public com.afarcloud.thrift.ObservationType getObservation() {
    return this.Observation;
  }

  /**
   * 
   * @see com.afarcloud.thrift.ObservationType
   */
  public ResponseNumber setObservation(com.afarcloud.thrift.ObservationType Observation) {
    this.Observation = Observation;
    return this;
  }

  public void unsetObservation() {
    this.Observation = null;
  }

  /** Returns true if field Observation is set (has been assigned a value) and false otherwise */
  public boolean isSetObservation() {
    return this.Observation != null;
  }

  public void setObservationIsSet(boolean value) {
    if (!value) {
      this.Observation = null;
    }
  }

  public String getUnits() {
    return this.Units;
  }

  public ResponseNumber setUnits(String Units) {
    this.Units = Units;
    return this;
  }

  public void unsetUnits() {
    this.Units = null;
  }

  /** Returns true if field Units is set (has been assigned a value) and false otherwise */
  public boolean isSetUnits() {
    return this.Units != null;
  }

  public void setUnitsIsSet(boolean value) {
    if (!value) {
      this.Units = null;
    }
  }

  public double getResult() {
    return this.Result;
  }

  public ResponseNumber setResult(double Result) {
    this.Result = Result;
    setResultIsSet(true);
    return this;
  }

  public void unsetResult() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __RESULT_ISSET_ID);
  }

  /** Returns true if field Result is set (has been assigned a value) and false otherwise */
  public boolean isSetResult() {
    return EncodingUtils.testBit(__isset_bitfield, __RESULT_ISSET_ID);
  }

  public void setResultIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __RESULT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TIMESTAMP:
      if (value == null) {
        unsetTimestamp();
      } else {
        setTimestamp((Long)value);
      }
      break;

    case OBSERVATION:
      if (value == null) {
        unsetObservation();
      } else {
        setObservation((com.afarcloud.thrift.ObservationType)value);
      }
      break;

    case UNITS:
      if (value == null) {
        unsetUnits();
      } else {
        setUnits((String)value);
      }
      break;

    case RESULT:
      if (value == null) {
        unsetResult();
      } else {
        setResult((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TIMESTAMP:
      return Long.valueOf(getTimestamp());

    case OBSERVATION:
      return getObservation();

    case UNITS:
      return getUnits();

    case RESULT:
      return Double.valueOf(getResult());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TIMESTAMP:
      return isSetTimestamp();
    case OBSERVATION:
      return isSetObservation();
    case UNITS:
      return isSetUnits();
    case RESULT:
      return isSetResult();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResponseNumber)
      return this.equals((ResponseNumber)that);
    return false;
  }

  public boolean equals(ResponseNumber that) {
    if (that == null)
      return false;

    boolean this_present_Timestamp = true;
    boolean that_present_Timestamp = true;
    if (this_present_Timestamp || that_present_Timestamp) {
      if (!(this_present_Timestamp && that_present_Timestamp))
        return false;
      if (this.Timestamp != that.Timestamp)
        return false;
    }

    boolean this_present_Observation = true && this.isSetObservation();
    boolean that_present_Observation = true && that.isSetObservation();
    if (this_present_Observation || that_present_Observation) {
      if (!(this_present_Observation && that_present_Observation))
        return false;
      if (!this.Observation.equals(that.Observation))
        return false;
    }

    boolean this_present_Units = true && this.isSetUnits();
    boolean that_present_Units = true && that.isSetUnits();
    if (this_present_Units || that_present_Units) {
      if (!(this_present_Units && that_present_Units))
        return false;
      if (!this.Units.equals(that.Units))
        return false;
    }

    boolean this_present_Result = true;
    boolean that_present_Result = true;
    if (this_present_Result || that_present_Result) {
      if (!(this_present_Result && that_present_Result))
        return false;
      if (this.Result != that.Result)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_Timestamp = true;
    list.add(present_Timestamp);
    if (present_Timestamp)
      list.add(Timestamp);

    boolean present_Observation = true && (isSetObservation());
    list.add(present_Observation);
    if (present_Observation)
      list.add(Observation.getValue());

    boolean present_Units = true && (isSetUnits());
    list.add(present_Units);
    if (present_Units)
      list.add(Units);

    boolean present_Result = true;
    list.add(present_Result);
    if (present_Result)
      list.add(Result);

    return list.hashCode();
  }

  @Override
  public int compareTo(ResponseNumber other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTimestamp()).compareTo(other.isSetTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Timestamp, other.Timestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetObservation()).compareTo(other.isSetObservation());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetObservation()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Observation, other.Observation);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUnits()).compareTo(other.isSetUnits());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUnits()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Units, other.Units);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResult()).compareTo(other.isSetResult());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResult()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Result, other.Result);
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
    StringBuilder sb = new StringBuilder("ResponseNumber(");
    boolean first = true;

    sb.append("Timestamp:");
    sb.append(this.Timestamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("Observation:");
    if (this.Observation == null) {
      sb.append("null");
    } else {
      sb.append(this.Observation);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("Units:");
    if (this.Units == null) {
      sb.append("null");
    } else {
      sb.append(this.Units);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("Result:");
    sb.append(this.Result);
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

  private static class ResponseNumberStandardSchemeFactory implements SchemeFactory {
    public ResponseNumberStandardScheme getScheme() {
      return new ResponseNumberStandardScheme();
    }
  }

  private static class ResponseNumberStandardScheme extends StandardScheme<ResponseNumber> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ResponseNumber struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TIMESTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.Timestamp = iprot.readI64();
              struct.setTimestampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // OBSERVATION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.Observation = com.afarcloud.thrift.ObservationType.findByValue(iprot.readI32());
              struct.setObservationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // UNITS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.Units = iprot.readString();
              struct.setUnitsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // RESULT
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.Result = iprot.readDouble();
              struct.setResultIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ResponseNumber struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(TIMESTAMP_FIELD_DESC);
      oprot.writeI64(struct.Timestamp);
      oprot.writeFieldEnd();
      if (struct.Observation != null) {
        oprot.writeFieldBegin(OBSERVATION_FIELD_DESC);
        oprot.writeI32(struct.Observation.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.Units != null) {
        oprot.writeFieldBegin(UNITS_FIELD_DESC);
        oprot.writeString(struct.Units);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(RESULT_FIELD_DESC);
      oprot.writeDouble(struct.Result);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ResponseNumberTupleSchemeFactory implements SchemeFactory {
    public ResponseNumberTupleScheme getScheme() {
      return new ResponseNumberTupleScheme();
    }
  }

  private static class ResponseNumberTupleScheme extends TupleScheme<ResponseNumber> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ResponseNumber struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetTimestamp()) {
        optionals.set(0);
      }
      if (struct.isSetObservation()) {
        optionals.set(1);
      }
      if (struct.isSetUnits()) {
        optionals.set(2);
      }
      if (struct.isSetResult()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetTimestamp()) {
        oprot.writeI64(struct.Timestamp);
      }
      if (struct.isSetObservation()) {
        oprot.writeI32(struct.Observation.getValue());
      }
      if (struct.isSetUnits()) {
        oprot.writeString(struct.Units);
      }
      if (struct.isSetResult()) {
        oprot.writeDouble(struct.Result);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ResponseNumber struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.Timestamp = iprot.readI64();
        struct.setTimestampIsSet(true);
      }
      if (incoming.get(1)) {
        struct.Observation = com.afarcloud.thrift.ObservationType.findByValue(iprot.readI32());
        struct.setObservationIsSet(true);
      }
      if (incoming.get(2)) {
        struct.Units = iprot.readString();
        struct.setUnitsIsSet(true);
      }
      if (incoming.get(3)) {
        struct.Result = iprot.readDouble();
        struct.setResultIsSet(true);
      }
    }
  }

}

