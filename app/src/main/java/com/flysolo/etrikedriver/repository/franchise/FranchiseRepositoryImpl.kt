package com.flysolo.etrikedriver.repository.franchise

import android.util.Log
import com.flysolo.etrikedriver.models.franchise.Franchise
import com.flysolo.etrikedriver.models.franchise.FranchiseStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

const val FRANCHISE_COLLECTION = "franchise"
class FranchiseRepositoryImpl(
    private  val auth  : FirebaseAuth,
    private val firestore: FirebaseFirestore
): FranchiseRepository {

    override suspend fun getFranchises(driverID: String): Result<List<Franchise>> {
        return try {
            val querySnapshot = firestore.collection(FRANCHISE_COLLECTION)
                .whereEqualTo("driverID", driverID)
                .whereEqualTo("status",FranchiseStatus.ACTIVE)
                .orderBy("createdAt",Query.Direction.DESCENDING)
                .orderBy("updatedAt",Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            val franchises = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Franchise::class.java)?.copy(id = doc.id)
            }
            Result.success(franchises)
        } catch (e: Exception) {
            Log.e("franchise",e.localizedMessage,e)
            Result.failure(e)
        }
    }
}