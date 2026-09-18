package com.example.yourapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PrivacyPolicyDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var hasScrolledToBottom by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // ✅ check if user scrolled to bottom
    LaunchedEffect(scrollState.value) {
        if (scrollState.value >= scrollState.maxValue - 10) {
            hasScrolledToBottom = true
        }
    }

    Dialog(
        onDismissRequest = { /* cannot dismiss without choosing */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // ─── Header ───────────────────────────
                Text(
                    text = "Privacy Policy &\nTerms and Conditions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "NutriDiet — Version 1.3",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // ─── Scrollable Content ───────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    PolicySection(
                        title = "1. Introduction",
                        content = "Welcome to NutriDiet, a mobile application " +
                                "developed as a capstone project by students of " +
                                "De La Salle Lipa. This Privacy Policy " +
                                "explains how we collect, use, and protect your " +
                                "information when you use our application."
                    )

                    PolicySection(
                        title = "2. Data We Collect",
                        content = "We collect the following anonymous health data:\n\n" +
                                "• Blood pressure readings (systolic and diastolic)\n" +
                                "• Hypertension classification level\n" +
                                "• Age and gender (for BMI and calorie calculation)\n" +
                                "• Activity level and calorie goals\n" +
                                "• Date and time of blood pressure readings\n\n" +
                                "We do NOT collect:\n" +
                                "• Your name or real identity\n" +
                                "• Phone number or email address\n" +
                                "• Location data\n" +
                                "• Payment information"
                    )

                    PolicySection(
                        title = "3. Anonymous User ID",
                        content = "You are assigned a randomly generated anonymous " +
                                "ID (e.g. UID0001) when you first use the app. " +
                                "This ID cannot be used to identify you personally. " +
                                "Your data is stored and analyzed using only this " +
                                "anonymous ID."
                    )

                    PolicySection(
                        title = "4. How We Use Your Data",
                        content = "Your anonymous data is used for:\n\n" +
                                "• Academic research on hypertension patterns " +
                                "in Filipino users\n" +
                                "• Improving diet plan recommendations\n" +
                                "• Capstone project analysis and reporting\n" +
                                "• Understanding hypertension trends\n\n" +
                                "Your data will NOT be:\n" +
                                "• Sold to third parties\n" +
                                "• Used for advertising purposes\n" +
                                "• Shared with insurance companies\n" +
                                "• Used to identify you personally"
                    )

                    PolicySection(
                        title = "5. Data Storage",
                        content = "Your anonymous data is stored securely on " +
                                "Google Firebase Firestore, which uses " +
                                "industry-standard encryption. Data is stored " +
                                "in servers located in Southeast Asia."
                    )

                    PolicySection(
                        title = "6. AI-Generated Content",
                        content = "Diet plans and health insights generated by " +
                                "this app use Google Gemini AI. These are for " +
                                "informational purposes only and do NOT replace " +
                                "professional medical advice. Always consult a " +
                                "licensed physician or registered dietitian " +
                                "before making dietary changes."
                    )

                    PolicySection(
                        title = "7. Medical Disclaimer",
                        content = "NutriDiet is NOT a medical device and does NOT " +
                                "provide medical diagnosis or treatment. The " +
                                "blood pressure classifications and diet " +
                                "recommendations are based on AHA 2017 guidelines " +
                                "and DASH diet research, and are for educational " +
                                "and informational purposes only.\n\n" +
                                "In case of a hypertensive crisis " +
                                "(BP ≥ 180/120 mmHg), seek IMMEDIATE emergency " +
                                "medical care."
                    )

                    PolicySection(
                        title = "8. Your Rights",
                        content = "You have the right to:\n\n" +
                                "• Decline data collection (app still works " +
                                "locally without uploading data)\n" +
                                "• Request deletion of your anonymous data by " +
                                "contacting us at\n" +
                                "christian_custodio@dlsl.edu.ph\n" +
                                "• Know how your data is being used"
                    )

                    PolicySection(
                        title = "9. Contact Us",
                        content = "For questions about this privacy policy, " +
                                "contact us at:\n" +
                                "Email:\n" +
                                "christian_custodio@dlsl.edu.ph\n" +
                                "Christian Custodio\n" +
                                "BSIT 4th year, IT4B"
                    )

                    PolicySection(
                        title = "10. Changes to This Policy",
                        content = "We may update this Privacy Policy as the app " +
                                "develops. We will notify you of significant " +
                                "changes through the app."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ scroll hint
                    if (!hasScrolledToBottom) {
                        Text(
                            text = "↓ Please scroll down to read the full policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // ─── Consent Text ─────────────────────
                Text(
                    text = "By tapping \"I Agree\", you consent to the " +
                            "collection of your anonymous health data for " +
                            "academic research purposes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ─── Buttons ──────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // decline — app works but no data upload
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decline")
                    }

                    // accept
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        enabled = hasScrolledToBottom  // ✅ must read first
                    ) {
                        Text(
                            if (hasScrolledToBottom) "I Agree"
                            else "Scroll to Accept"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}