package com.example.medipal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class Medicine(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("brandName")
    val brandName: String,
    
    @SerializedName("drugName")
    val drugName: String,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("strength")
    val strength: String?,
    
    @SerializedName("manufacturer")
    val manufacturer: String?,
    
    @SerializedName("description")
    val description: String?
)

data class MedicineTiming(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("medicineListId")
    val medicineListId: String,
    
    @SerializedName("timeOfDay")
    val timeOfDay: String,
    
    @SerializedName("dosage")
    val dosage: Int
)

data class PrescriptionMedicine(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("prescriptionId")
    val prescriptionId: String,
    
    @SerializedName("medicineId")
    val medicineId: String,
    
    @SerializedName("duration")
    val duration: Int?,
    
    @SerializedName("durationType")
    val durationType: String?,
    
    @SerializedName("beforeFood")
    val beforeFood: Boolean,
    
    @SerializedName("additionalNotes")
    val additionalNotes: String?,
    
    @SerializedName("medicine")
    val medicine: Medicine,
    
    @SerializedName("times")
    val times: List<MedicineTiming>
)

data class Doctor(
    @SerializedName("Name")
    val name: String,
    
    @SerializedName("Registration_No")
    val registrationNo: String,
    
    @SerializedName("Specialisation")
    val specialisation: String,
    
    @SerializedName("ContactNumber")
    val contactNumber: String
)

data class Patient(
    @SerializedName("Name")
    val name: String,
    
    @SerializedName("PhoneNumber")
    val phoneNumber: String,
    
    @SerializedName("Age")
    val age: Int?,
    
    @SerializedName("Gender")
    val gender: String?,
    
    @SerializedName("City")
    val city: String?,
    
    @SerializedName("State")
    val state: String?,
    
    @SerializedName("Country")
    val country: String?
)

@Entity(tableName = "prescriptions")
@TypeConverters(PrescriptionConverters::class)
data class Prescription(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    
    @SerializedName("isApproved")
    val isApproved: Boolean,
    
    @SerializedName("createdOn")
    val createdOn: String,
    
    @SerializedName("updatedOn")
    val updatedOn: String,
    
    @SerializedName("isUsedBy")
    val isUsedBy: String,
    
    @SerializedName("expiryDate")
    val expiryDate: String?,
    
    @SerializedName("Doctor")
    val doctorId: String,
    
    @SerializedName("Presciber")
    val prescriber: String,
    
    @SerializedName("diagnosis")
    val diagnosis: String,
    
    @SerializedName("symptoms")
    val symptoms: String?,
    
    @SerializedName("additionalNotes")
    val additionalNotes: String?,
    
    @SerializedName("doctor_regNo")
    val doctor: Doctor,
    
    @SerializedName("patient_contact")
    val patient: Patient,
    
    @SerializedName("medicine_list")
    val medicineList: List<PrescriptionMedicine>
)

class PrescriptionConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromMedicineList(medicineList: List<PrescriptionMedicine>): String {
        return gson.toJson(medicineList)
    }

    @TypeConverter
    fun toMedicineList(medicineListString: String): List<PrescriptionMedicine> {
        val type = object : TypeToken<List<PrescriptionMedicine>>() {}.type
        return gson.fromJson(medicineListString, type)
    }

    @TypeConverter
    fun fromDoctor(doctor: Doctor): String {
        return gson.toJson(doctor)
    }

    @TypeConverter
    fun toDoctor(doctorString: String): Doctor {
        return gson.fromJson(doctorString, Doctor::class.java)
    }

    @TypeConverter
    fun fromPatient(patient: Patient): String {
        return gson.toJson(patient)
    }

    @TypeConverter
    fun toPatient(patientString: String): Patient {
        return gson.fromJson(patientString, Patient::class.java)
    }
} 