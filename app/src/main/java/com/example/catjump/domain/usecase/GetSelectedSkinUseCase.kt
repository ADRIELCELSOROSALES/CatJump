package com.example.catjump.domain.usecase

import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSelectedSkinUseCase(
    private val scoreRepository: ScoreRepository
) {
    operator fun invoke(): Flow<CatSkin> = scoreRepository.getSelectedSkinId()
        .map { skinId -> CatSkins.getById(skinId) }
}
