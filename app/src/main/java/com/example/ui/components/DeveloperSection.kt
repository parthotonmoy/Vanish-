package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.VeilTheme

@Composable
fun DeveloperSection(
    modifier: Modifier = Modifier
) {
    val colors = VeilTheme.colors
    val typography = VeilTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 24.dp)
            .testTag("developer_section")
    ) {
        Text(
            text = stringResource(R.string.section_developer),
            style = typography.sectionTitle,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Field 1: Developer
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${stringResource(R.string.developer_name_label)} — ",
                style = typography.label,
                color = colors.textSecondary
            )
            Text(
                text = stringResource(R.string.developer_name_value),
                style = typography.body,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Field 2: University
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${stringResource(R.string.developer_university_label)} — ",
                style = typography.label,
                color = colors.textSecondary
            )
            Text(
                text = stringResource(R.string.developer_university_value),
                style = typography.body,
                color = colors.textPrimary
            )
        }
    }
}
