package com.example.a207408_cikguizwan_lab02

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.a207408_cikguizwan_lab02.ui.theme.WildLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: WildLensViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var nameInput by remember(profile.name) { mutableStateOf(profile.name) }
    var locationInput by remember(profile.location) { mutableStateOf(profile.location) }
    var isLocating by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    // 权限请求 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            isLocating = true
            locationError = false
            LocationHelper.getCurrentLocation(
                context = context,
                onSuccess = { name, _, _ ->
                    locationInput = name
                    isLocating = false
                },
                onFailure = {
                    isLocating = false
                    locationError = true
                }
            )
        } else {
            locationError = true
        }
    }

    WildLensTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Profile", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 当前 profile 预览
                if (profile.name.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Current Profile", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            Text("👤 ${profile.name}", style = MaterialTheme.typography.titleMedium)
                            Text("📍 ${profile.location}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Text("Update Profile", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Location 输入框 + GPS 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("Your Location") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    // GPS 定位按钮
                    FilledTonalIconButton(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .padding(top = 8.dp),
                        enabled = !isLocating
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = "Use my location",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 权限被拒绝时的提示
                if (locationError) {
                    Text(
                        text = "⚠ Location access denied. Please enable it in Settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = { viewModel.updateProfile(nameInput, locationInput) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = nameInput.isNotBlank()
                ) {
                    Text("Save Profile", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Back", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}