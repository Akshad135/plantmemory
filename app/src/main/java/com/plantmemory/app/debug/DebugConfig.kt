package com.plantmemory.app.debug

/**
 * Debug configuration for testing purposes.
 * 
 * Toggle USE_DUMMY_DATA to true and rebuild the app to populate
 * the database with test entries for 2023, 2024, and 2025.
 * 
 * WARNING: Data is inserted into the production database.
 * Uninstall the app to clear dummy data when done testing.
 */
object DebugConfig {
    /**
     * Kill switch for dummy data.
     * - true: Seed database with 665 days of test entries on first launch
     * - false: Normal app behavior (default)
     */
    const val USE_DUMMY_DATA = false
}
