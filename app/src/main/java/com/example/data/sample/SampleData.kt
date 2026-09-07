package com.example.data.sample

import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.Category
import com.example.domain.models.EditableField
import com.example.domain.models.ElementPermissions
import com.example.domain.models.ElementType
import com.example.domain.models.FieldType
import com.example.domain.models.PosterBackground
import com.example.domain.models.ShapeType
import com.example.domain.models.Template
import com.example.domain.models.TemplatePermissionLevel
import com.example.domain.models.TemplateStatus

object SampleData {

    val categories = listOf(
        Category("cat_all", "All", "GridView", "All categories", 12),
        Category("cat_business", "Business", "Business", "Corporate & Marketing", 28, "#1E3A8A"),
        Category("cat_events", "Events", "Event", "Conferences & Shows", 22, "#7C3AED"),
        Category("cat_food", "Food & Cafe", "Restaurant", "Menus & Discounts", 19, "#D97706"),
        Category("cat_festivals", "Festivals", "Celebration", "Holiday Greetings", 15, "#DC2626"),
        Category("cat_education", "Education", "School", "Admissions & Workshops", 14, "#059669"),
        Category("cat_real_estate", "Real Estate", "Home", "Property Sales & Open House", 11, "#0D9488"),
        Category("cat_fitness", "Fitness & Gym", "FitnessCenter", "Training & Health", 9, "#EA580C"),
        Category("cat_birthday", "Birthday", "Cake", "Parties & Invitations", 16, "#DB2777"),
        Category("cat_offers", "Special Offers", "LocalOffer", "Flash Sales & Discounts", 25, "#E11D48")
    )

    fun getInitialTemplates(): List<Template> = listOf(
        // 1. Summer Music Festival (Square)
        Template(
            templateId = "tmpl_summer_fest",
            creatorId = "creator_studio_x",
            creatorName = "Studio X",
            name = "Summer Music Festival",
            description = "Vibrant gradient poster for live concerts, DJ nights, and musical events.",
            categoryId = "cat_events",
            categoryName = "Events",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#FF6B6B",
                color2Hex = "#4ECDC4",
                angleDegrees = 135f
            ),
            isFeatured = true,
            isPro = true,
            usageCount = 1420,
            favoriteCount = 380,
            rating = 4.9f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_fest_bg_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#FFFFFF",
                    opacity = 0.92f,
                    shapeCornerRadiusDp = 24f,
                    xRatio = 0.08f,
                    yRatio = 0.08f,
                    widthRatio = 0.84f,
                    heightRatio = 0.84f,
                    layerOrder = 0,
                    isLocked = true
                ),
                CanvasElement(
                    id = "el_fest_tag",
                    type = ElementType.TEXT,
                    text = "ANNUAL LIVE EXPERIENCE",
                    fontSizeSp = 12f,
                    fontColorHex = "#FF6B6B",
                    isBold = true,
                    letterSpacingSp = 2f,
                    xRatio = 0.15f,
                    yRatio = 0.14f,
                    widthRatio = 0.70f,
                    heightRatio = 0.05f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_fest_title",
                    type = ElementType.TEXT,
                    text = "SUMMER SOUNDWAVE 2026",
                    fontSizeSp = 26f,
                    fontColorHex = "#111827",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_event_name",
                    xRatio = 0.12f,
                    yRatio = 0.20f,
                    widthRatio = 0.76f,
                    heightRatio = 0.12f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_fest_sub",
                    type = ElementType.TEXT,
                    text = "Featuring Top International DJs & Live Acoustic Bands",
                    fontSizeSp = 14f,
                    fontColorHex = "#4B5563",
                    isEditableField = true,
                    editableFieldId = "field_tagline",
                    xRatio = 0.15f,
                    yRatio = 0.33f,
                    widthRatio = 0.70f,
                    heightRatio = 0.08f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_fest_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#111827",
                    shapeCornerRadiusDp = 12f,
                    xRatio = 0.25f,
                    yRatio = 0.44f,
                    widthRatio = 0.50f,
                    heightRatio = 0.09f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_fest_date",
                    type = ElementType.TEXT,
                    text = "AUG 28-30 • 6:00 PM ONWARDS",
                    fontSizeSp = 13f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1f,
                    isEditableField = true,
                    editableFieldId = "field_date_time",
                    xRatio = 0.26f,
                    yRatio = 0.46f,
                    widthRatio = 0.48f,
                    heightRatio = 0.05f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_fest_loc",
                    type = ElementType.TEXT,
                    text = "Grand Oceanfront Arena, Marina Bay",
                    fontSizeSp = 14f,
                    fontColorHex = "#1F2937",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_location",
                    xRatio = 0.15f,
                    yRatio = 0.57f,
                    widthRatio = 0.70f,
                    heightRatio = 0.06f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_fest_tickets",
                    type = ElementType.TEXT,
                    text = "Tickets start at $49 • Book at soundwavefest.com",
                    fontSizeSp = 12f,
                    fontColorHex = "#6B7280",
                    isEditableField = true,
                    editableFieldId = "field_ticket_info",
                    xRatio = 0.15f,
                    yRatio = 0.65f,
                    widthRatio = 0.70f,
                    heightRatio = 0.06f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_fest_contact",
                    type = ElementType.TEXT,
                    text = "VIP RSVP: +1 (800) 555-0199",
                    fontSizeSp = 13f,
                    fontColorHex = "#111827",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_contact_phone",
                    xRatio = 0.15f,
                    yRatio = 0.75f,
                    widthRatio = 0.70f,
                    heightRatio = 0.05f,
                    layerOrder = 8
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_event_name",
                    elementId = "el_fest_title",
                    type = FieldType.TEXT,
                    label = "Event Title",
                    placeholder = "e.g., Summer Soundwave 2026",
                    defaultValue = "SUMMER SOUNDWAVE 2026"
                ),
                EditableField(
                    fieldId = "field_tagline",
                    elementId = "el_fest_sub",
                    type = FieldType.TEXT,
                    label = "Event Subtitle / Artists",
                    placeholder = "e.g., Featuring Top DJs",
                    defaultValue = "Featuring Top International DJs & Live Acoustic Bands"
                ),
                EditableField(
                    fieldId = "field_date_time",
                    elementId = "el_fest_date",
                    type = FieldType.DATE,
                    label = "Date & Time",
                    placeholder = "e.g., AUG 28-30 • 6:00 PM",
                    defaultValue = "AUG 28-30 • 6:00 PM ONWARDS"
                ),
                EditableField(
                    fieldId = "field_location",
                    elementId = "el_fest_loc",
                    type = FieldType.ADDRESS,
                    label = "Venue Address",
                    placeholder = "e.g., Grand Oceanfront Arena",
                    defaultValue = "Grand Oceanfront Arena, Marina Bay"
                ),
                EditableField(
                    fieldId = "field_ticket_info",
                    elementId = "el_fest_tickets",
                    type = FieldType.WEBSITE,
                    label = "Ticket Info / Website",
                    placeholder = "e.g., Book at website.com",
                    defaultValue = "Tickets start at $49 • Book at soundwavefest.com"
                ),
                EditableField(
                    fieldId = "field_contact_phone",
                    elementId = "el_fest_contact",
                    type = FieldType.PHONE,
                    label = "Contact Phone",
                    placeholder = "e.g., +1 (800) 555-0199",
                    defaultValue = "VIP RSVP: +1 (800) 555-0199"
                )
            )
        ),

        // 2. Corporate Business Webinar (Story / 9:16)
        Template(
            templateId = "tmpl_corp_webinar",
            creatorId = "creator_brand_pro",
            creatorName = "BrandPro Agency",
            name = "Corporate Growth Webinar",
            description = "High-converting webinar and business keynote flyer with speaker profile.",
            categoryId = "cat_business",
            categoryName = "Business",
            canvasWidth = 1080,
            canvasHeight = 1920,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#0F172A",
                color2Hex = "#1E3A8A",
                angleDegrees = 180f
            ),
            isFeatured = true,
            isPro = false,
            usageCount = 980,
            favoriteCount = 295,
            rating = 4.8f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_corp_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#3B82F6",
                    shapeCornerRadiusDp = 8f,
                    xRatio = 0.1f,
                    yRatio = 0.08f,
                    widthRatio = 0.35f,
                    heightRatio = 0.035f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_corp_live_text",
                    type = ElementType.TEXT,
                    text = "FREE LIVE MASTERCLASS",
                    fontSizeSp = 11f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1f,
                    xRatio = 0.11f,
                    yRatio = 0.087f,
                    widthRatio = 0.33f,
                    heightRatio = 0.025f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_corp_title",
                    type = ElementType.TEXT,
                    text = "SCALING B2B SAAS TO $10M ARR",
                    fontSizeSp = 28f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_webinar_title",
                    xRatio = 0.1f,
                    yRatio = 0.14f,
                    widthRatio = 0.8f,
                    heightRatio = 0.12f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_corp_desc",
                    type = ElementType.TEXT,
                    text = "Learn modern customer acquisition strategies, sales funnels, and enterprise retention tactics from proven founders.",
                    fontSizeSp = 14f,
                    fontColorHex = "#94A3B8",
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_webinar_desc",
                    xRatio = 0.1f,
                    yRatio = 0.27f,
                    widthRatio = 0.8f,
                    heightRatio = 0.09f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_corp_speaker_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#1E293B",
                    strokeWidthDp = 1f,
                    strokeColorHex = "#334155",
                    shapeCornerRadiusDp = 16f,
                    xRatio = 0.1f,
                    yRatio = 0.39f,
                    widthRatio = 0.8f,
                    heightRatio = 0.18f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_corp_speaker_name",
                    type = ElementType.TEXT,
                    text = "Sarah Jenkins",
                    fontSizeSp = 18f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_speaker_name",
                    xRatio = 0.15f,
                    yRatio = 0.42f,
                    widthRatio = 0.7f,
                    heightRatio = 0.04f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_corp_speaker_title",
                    type = ElementType.TEXT,
                    text = "VP of Growth @ Apex Enterprises (Ex-Stripe)",
                    fontSizeSp = 13f,
                    fontColorHex = "#38BDF8",
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_speaker_title",
                    xRatio = 0.15f,
                    yRatio = 0.47f,
                    widthRatio = 0.7f,
                    heightRatio = 0.04f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_corp_date_time",
                    type = ElementType.TEXT,
                    text = "🗓 Thursday, Oct 15 • 2:00 PM EST",
                    fontSizeSp = 15f,
                    fontColorHex = "#F8FAFC",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_webinar_time",
                    xRatio = 0.1f,
                    yRatio = 0.62f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 8
                ),
                CanvasElement(
                    id = "el_corp_btn",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#2563EB",
                    shapeCornerRadiusDp = 14f,
                    xRatio = 0.1f,
                    yRatio = 0.72f,
                    widthRatio = 0.8f,
                    heightRatio = 0.07f,
                    layerOrder = 9
                ),
                CanvasElement(
                    id = "el_corp_btn_txt",
                    type = ElementType.TEXT,
                    text = "REGISTER FREE TODAY",
                    fontSizeSp = 16f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1f,
                    xRatio = 0.15f,
                    yRatio = 0.735f,
                    widthRatio = 0.7f,
                    heightRatio = 0.04f,
                    layerOrder = 10
                ),
                CanvasElement(
                    id = "el_corp_link",
                    type = ElementType.TEXT,
                    text = "www.apexmastery.io/webinar",
                    fontSizeSp = 12f,
                    fontColorHex = "#94A3B8",
                    isEditableField = true,
                    editableFieldId = "field_webinar_link",
                    xRatio = 0.1f,
                    yRatio = 0.82f,
                    widthRatio = 0.8f,
                    heightRatio = 0.04f,
                    layerOrder = 11
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_webinar_title",
                    elementId = "el_corp_title",
                    type = FieldType.TEXT,
                    label = "Webinar Topic",
                    placeholder = "e.g., Scaling B2B SaaS to $10M ARR",
                    defaultValue = "SCALING B2B SAAS TO $10M ARR"
                ),
                EditableField(
                    fieldId = "field_webinar_desc",
                    elementId = "el_corp_desc",
                    type = FieldType.TEXT,
                    label = "Description / Takeaways",
                    placeholder = "e.g., Key takeaways...",
                    defaultValue = "Learn modern customer acquisition strategies, sales funnels, and enterprise retention tactics."
                ),
                EditableField(
                    fieldId = "field_speaker_name",
                    elementId = "el_corp_speaker_name",
                    type = FieldType.TEXT,
                    label = "Keynote Speaker Name",
                    placeholder = "e.g., Sarah Jenkins",
                    defaultValue = "Sarah Jenkins"
                ),
                EditableField(
                    fieldId = "field_speaker_title",
                    elementId = "el_corp_speaker_title",
                    type = FieldType.TEXT,
                    label = "Speaker Designation & Company",
                    placeholder = "e.g., VP of Growth @ Apex",
                    defaultValue = "VP of Growth @ Apex Enterprises (Ex-Stripe)"
                ),
                EditableField(
                    fieldId = "field_webinar_time",
                    elementId = "el_corp_date_time",
                    type = FieldType.DATE,
                    label = "Date & Schedule",
                    placeholder = "e.g., Thursday, Oct 15 • 2:00 PM EST",
                    defaultValue = "🗓 Thursday, Oct 15 • 2:00 PM EST"
                ),
                EditableField(
                    fieldId = "field_webinar_link",
                    elementId = "el_corp_link",
                    type = FieldType.WEBSITE,
                    label = "Registration URL",
                    placeholder = "e.g., www.apexmastery.io",
                    defaultValue = "www.apexmastery.io/webinar"
                )
            )
        ),

        // 3. Cafe Special Discount Offer (Square)
        Template(
            templateId = "tmpl_cafe_offer",
            creatorId = "creator_design_lab",
            creatorName = "DesignLab Co",
            name = "Artisan Cafe Special Offer",
            description = "Appetizing coffee & bakery discount banner with custom coupon text.",
            categoryId = "cat_food",
            categoryName = "Food & Cafe",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.SOLID,
                color1Hex = "#FFFBEB"
            ),
            isFeatured = true,
            isPro = false,
            usageCount = 2150,
            favoriteCount = 610,
            rating = 4.9f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_cafe_border",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#FFFFFF",
                    strokeWidthDp = 2f,
                    strokeColorHex = "#D97706",
                    shapeCornerRadiusDp = 20f,
                    xRatio = 0.06f,
                    yRatio = 0.06f,
                    widthRatio = 0.88f,
                    heightRatio = 0.88f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_cafe_name",
                    type = ElementType.TEXT,
                    text = "THE DAILY ROAST CAFE",
                    fontSizeSp = 14f,
                    fontColorHex = "#92400E",
                    isBold = true,
                    letterSpacingSp = 2f,
                    isEditableField = true,
                    editableFieldId = "field_cafe_name",
                    xRatio = 0.12f,
                    yRatio = 0.12f,
                    widthRatio = 0.76f,
                    heightRatio = 0.05f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_cafe_deal",
                    type = ElementType.TEXT,
                    text = "BUY 1 GET 1 FREE",
                    fontSizeSp = 30f,
                    fontColorHex = "#78350F",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_deal_title",
                    xRatio = 0.1f,
                    yRatio = 0.22f,
                    widthRatio = 0.8f,
                    heightRatio = 0.10f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_cafe_sub",
                    type = ElementType.TEXT,
                    text = "On All Hand-Crafted Specialty Brews & French Pastries",
                    fontSizeSp = 14f,
                    fontColorHex = "#A16207",
                    isEditableField = true,
                    editableFieldId = "field_deal_sub",
                    xRatio = 0.12f,
                    yRatio = 0.35f,
                    widthRatio = 0.76f,
                    heightRatio = 0.07f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_cafe_code_box",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#F59E0B",
                    shapeCornerRadiusDp = 10f,
                    xRatio = 0.25f,
                    yRatio = 0.48f,
                    widthRatio = 0.5f,
                    heightRatio = 0.08f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_cafe_code",
                    type = ElementType.TEXT,
                    text = "PROMO CODE: ROAST50",
                    fontSizeSp = 14f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1.5f,
                    isEditableField = true,
                    editableFieldId = "field_promo_code",
                    xRatio = 0.26f,
                    yRatio = 0.50f,
                    widthRatio = 0.48f,
                    heightRatio = 0.04f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_cafe_valid",
                    type = ElementType.TEXT,
                    text = "Valid until Sunday • Dine-in & Takeaway",
                    fontSizeSp = 12f,
                    fontColorHex = "#B45309",
                    isEditableField = true,
                    editableFieldId = "field_validity",
                    xRatio = 0.15f,
                    yRatio = 0.60f,
                    widthRatio = 0.7f,
                    heightRatio = 0.05f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_cafe_address",
                    type = ElementType.TEXT,
                    text = "📍 404 Market Street, Downtown Arts District",
                    fontSizeSp = 13f,
                    fontColorHex = "#78350F",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_cafe_address",
                    xRatio = 0.1f,
                    yRatio = 0.72f,
                    widthRatio = 0.8f,
                    heightRatio = 0.06f,
                    layerOrder = 8
                ),
                CanvasElement(
                    id = "el_cafe_phone",
                    type = ElementType.TEXT,
                    text = "Call for Reservations: (555) 321-4567",
                    fontSizeSp = 12f,
                    fontColorHex = "#92400E",
                    isEditableField = true,
                    editableFieldId = "field_cafe_phone",
                    xRatio = 0.1f,
                    yRatio = 0.80f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 9
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_cafe_name",
                    elementId = "el_cafe_name",
                    type = FieldType.TEXT,
                    label = "Cafe / Restaurant Name",
                    defaultValue = "THE DAILY ROAST CAFE"
                ),
                EditableField(
                    fieldId = "field_deal_title",
                    elementId = "el_cafe_deal",
                    type = FieldType.TEXT,
                    label = "Headline Offer",
                    defaultValue = "BUY 1 GET 1 FREE"
                ),
                EditableField(
                    fieldId = "field_deal_sub",
                    elementId = "el_cafe_sub",
                    type = FieldType.TEXT,
                    label = "Applicable Menu Items",
                    defaultValue = "On All Hand-Crafted Specialty Brews & French Pastries"
                ),
                EditableField(
                    fieldId = "field_promo_code",
                    elementId = "el_cafe_code",
                    type = FieldType.TEXT,
                    label = "Coupon / Promo Code",
                    defaultValue = "PROMO CODE: ROAST50"
                ),
                EditableField(
                    fieldId = "field_validity",
                    elementId = "el_cafe_valid",
                    type = FieldType.DATE,
                    label = "Offer Validity",
                    defaultValue = "Valid until Sunday • Dine-in & Takeaway"
                ),
                EditableField(
                    fieldId = "field_cafe_address",
                    elementId = "el_cafe_address",
                    type = FieldType.ADDRESS,
                    label = "Location Address",
                    defaultValue = "📍 404 Market Street, Downtown Arts District"
                ),
                EditableField(
                    fieldId = "field_cafe_phone",
                    elementId = "el_cafe_phone",
                    type = FieldType.PHONE,
                    label = "Phone / Reservations",
                    defaultValue = "Call for Reservations: (555) 321-4567"
                )
            )
        ),

        // 4. Real Estate Luxury Villa (Portrait / 4:5)
        Template(
            templateId = "tmpl_real_estate",
            creatorId = "creator_studio_x",
            creatorName = "Studio X",
            name = "Luxury Villa For Sale",
            description = "High-end minimalist real estate listing with specs and agent details.",
            categoryId = "cat_real_estate",
            categoryName = "Real Estate",
            canvasWidth = 1080,
            canvasHeight = 1350,
            background = PosterBackground(
                type = BackgroundType.SOLID,
                color1Hex = "#0F172A"
            ),
            isFeatured = false,
            isPro = true,
            usageCount = 740,
            favoriteCount = 210,
            rating = 4.7f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_re_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#D97706",
                    shapeCornerRadiusDp = 6f,
                    xRatio = 0.1f,
                    yRatio = 0.08f,
                    widthRatio = 0.28f,
                    heightRatio = 0.035f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_re_badge_txt",
                    type = ElementType.TEXT,
                    text = "EXCLUSIVE LISTING",
                    fontSizeSp = 10f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1f,
                    xRatio = 0.11f,
                    yRatio = 0.087f,
                    widthRatio = 0.26f,
                    heightRatio = 0.02f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_re_title",
                    type = ElementType.TEXT,
                    text = "THE AZURE HORIZON VILLA",
                    fontSizeSp = 24f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_property_name",
                    xRatio = 0.1f,
                    yRatio = 0.14f,
                    widthRatio = 0.8f,
                    heightRatio = 0.08f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_re_loc",
                    type = ElementType.TEXT,
                    text = "Palm Jumeirah Coastline, Sector 4",
                    fontSizeSp = 14f,
                    fontColorHex = "#94A3B8",
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_property_loc",
                    xRatio = 0.1f,
                    yRatio = 0.23f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_re_price_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#1E293B",
                    shapeCornerRadiusDp = 12f,
                    xRatio = 0.1f,
                    yRatio = 0.32f,
                    widthRatio = 0.8f,
                    heightRatio = 0.12f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_re_price",
                    type = ElementType.TEXT,
                    text = "$2,450,000",
                    fontSizeSp = 28f,
                    fontColorHex = "#F59E0B",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_price",
                    xRatio = 0.15f,
                    yRatio = 0.34f,
                    widthRatio = 0.7f,
                    heightRatio = 0.05f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_re_specs",
                    type = ElementType.TEXT,
                    text = "5 Bedrooms • 6 Bathrooms • Private Infinity Pool • 6,500 Sq Ft",
                    fontSizeSp = 12f,
                    fontColorHex = "#CBD5E1",
                    isEditableField = true,
                    editableFieldId = "field_specs",
                    xRatio = 0.15f,
                    yRatio = 0.40f,
                    widthRatio = 0.7f,
                    heightRatio = 0.04f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_re_agent_name",
                    type = ElementType.TEXT,
                    text = "Presented by Alexander Vance",
                    fontSizeSp = 15f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_agent_name",
                    xRatio = 0.1f,
                    yRatio = 0.62f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 8
                ),
                CanvasElement(
                    id = "el_re_agency",
                    type = ElementType.TEXT,
                    text = "Prime Haven Realty Group",
                    fontSizeSp = 13f,
                    fontColorHex = "#94A3B8",
                    isEditableField = true,
                    editableFieldId = "field_agency_name",
                    xRatio = 0.1f,
                    yRatio = 0.68f,
                    widthRatio = 0.8f,
                    heightRatio = 0.04f,
                    layerOrder = 9
                ),
                CanvasElement(
                    id = "el_re_contact",
                    type = ElementType.TEXT,
                    text = "📞 +1 (555) 789-0123 • inquiries@primehaven.com",
                    fontSizeSp = 13f,
                    fontColorHex = "#F59E0B",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_agent_contact",
                    xRatio = 0.1f,
                    yRatio = 0.76f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 10
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_property_name",
                    elementId = "el_re_title",
                    type = FieldType.TEXT,
                    label = "Property Title",
                    defaultValue = "THE AZURE HORIZON VILLA"
                ),
                EditableField(
                    fieldId = "field_property_loc",
                    elementId = "el_re_loc",
                    type = FieldType.ADDRESS,
                    label = "Location / Area",
                    defaultValue = "Palm Jumeirah Coastline, Sector 4"
                ),
                EditableField(
                    fieldId = "field_price",
                    elementId = "el_re_price",
                    type = FieldType.PRICE,
                    label = "Asking Price",
                    defaultValue = "$2,450,000"
                ),
                EditableField(
                    fieldId = "field_specs",
                    elementId = "el_re_specs",
                    type = FieldType.TEXT,
                    label = "Property Specifications",
                    defaultValue = "5 Bedrooms • 6 Bathrooms • Private Infinity Pool • 6,500 Sq Ft"
                ),
                EditableField(
                    fieldId = "field_agent_name",
                    elementId = "el_re_agent_name",
                    type = FieldType.TEXT,
                    label = "Listing Agent Name",
                    defaultValue = "Presented by Alexander Vance"
                ),
                EditableField(
                    fieldId = "field_agency_name",
                    elementId = "el_re_agency",
                    type = FieldType.TEXT,
                    label = "Real Estate Agency",
                    defaultValue = "Prime Haven Realty Group"
                ),
                EditableField(
                    fieldId = "field_agent_contact",
                    elementId = "el_re_contact",
                    type = FieldType.PHONE,
                    label = "Agent Phone & Email",
                    defaultValue = "📞 +1 (555) 789-0123 • inquiries@primehaven.com"
                )
            )
        ),

        // 5. School & College Admission Open (Flyer / A4)
        Template(
            templateId = "tmpl_school_admission",
            creatorId = "creator_brand_pro",
            creatorName = "BrandPro Agency",
            name = "Academy Admission Open",
            description = "Professional educational admission announcement with scholarship badges.",
            categoryId = "cat_education",
            categoryName = "Education",
            canvasWidth = 1240,
            canvasHeight = 1754,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#064E3B",
                color2Hex = "#047857",
                angleDegrees = 135f
            ),
            isFeatured = false,
            isPro = false,
            usageCount = 1120,
            favoriteCount = 310,
            rating = 4.8f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_edu_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#FFFFFF",
                    shapeCornerRadiusDp = 24f,
                    xRatio = 0.07f,
                    yRatio = 0.06f,
                    widthRatio = 0.86f,
                    heightRatio = 0.88f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_edu_inst",
                    type = ElementType.TEXT,
                    text = "CAMBRIDGE GLOBAL ACADEMY",
                    fontSizeSp = 14f,
                    fontColorHex = "#065F46",
                    isBold = true,
                    letterSpacingSp = 2f,
                    isEditableField = true,
                    editableFieldId = "field_school_name",
                    xRatio = 0.12f,
                    yRatio = 0.11f,
                    widthRatio = 0.76f,
                    heightRatio = 0.04f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_edu_title",
                    type = ElementType.TEXT,
                    text = "ADMISSIONS OPEN 2026-27",
                    fontSizeSp = 26f,
                    fontColorHex = "#111827",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_adm_title",
                    xRatio = 0.1f,
                    yRatio = 0.17f,
                    widthRatio = 0.8f,
                    heightRatio = 0.08f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_edu_sub",
                    type = ElementType.TEXT,
                    text = "Nursery to Grade 12 • IB & Cambridge Curriculum",
                    fontSizeSp = 14f,
                    fontColorHex = "#4B5563",
                    isEditableField = true,
                    editableFieldId = "field_curriculum",
                    xRatio = 0.12f,
                    yRatio = 0.27f,
                    widthRatio = 0.76f,
                    heightRatio = 0.05f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_edu_box",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#ECFDF5",
                    strokeWidthDp = 1.5f,
                    strokeColorHex = "#10B981",
                    shapeCornerRadiusDp = 16f,
                    xRatio = 0.12f,
                    yRatio = 0.35f,
                    widthRatio = 0.76f,
                    heightRatio = 0.16f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_edu_perks",
                    type = ElementType.TEXT,
                    text = "✓ 100% Merit Scholarships Available\n✓ State-of-the-Art AI & Robotics Labs\n✓ Olympic-Size Sports Infrastructure\n✓ Global University Placements",
                    fontSizeSp = 13f,
                    fontColorHex = "#065F46",
                    textAlign = "LEFT",
                    isEditableField = true,
                    editableFieldId = "field_highlights",
                    xRatio = 0.16f,
                    yRatio = 0.37f,
                    widthRatio = 0.68f,
                    heightRatio = 0.12f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_edu_phone",
                    type = ElementType.TEXT,
                    text = "Admission Helpline: +1 (800) 920-4321",
                    fontSizeSp = 14f,
                    fontColorHex = "#111827",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_school_phone",
                    xRatio = 0.12f,
                    yRatio = 0.62f,
                    widthRatio = 0.76f,
                    heightRatio = 0.04f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_edu_website",
                    type = ElementType.TEXT,
                    text = "Apply online: www.cambridgeglobal.edu",
                    fontSizeSp = 13f,
                    fontColorHex = "#059669",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_school_website",
                    xRatio = 0.12f,
                    yRatio = 0.68f,
                    widthRatio = 0.76f,
                    heightRatio = 0.04f,
                    layerOrder = 8
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_school_name",
                    elementId = "el_edu_inst",
                    type = FieldType.TEXT,
                    label = "School / University Name",
                    defaultValue = "CAMBRIDGE GLOBAL ACADEMY"
                ),
                EditableField(
                    fieldId = "field_adm_title",
                    elementId = "el_edu_title",
                    type = FieldType.TEXT,
                    label = "Admission Title",
                    defaultValue = "ADMISSIONS OPEN 2026-27"
                ),
                EditableField(
                    fieldId = "field_curriculum",
                    elementId = "el_edu_sub",
                    type = FieldType.TEXT,
                    label = "Grades & Curriculum",
                    defaultValue = "Nursery to Grade 12 • IB & Cambridge Curriculum"
                ),
                EditableField(
                    fieldId = "field_highlights",
                    elementId = "el_edu_perks",
                    type = FieldType.TEXT,
                    label = "Key Highlights",
                    defaultValue = "✓ 100% Merit Scholarships Available\n✓ State-of-the-Art AI & Robotics Labs\n✓ Olympic-Size Sports Infrastructure\n✓ Global University Placements"
                ),
                EditableField(
                    fieldId = "field_school_phone",
                    elementId = "el_edu_phone",
                    type = FieldType.PHONE,
                    label = "Admission Helpline",
                    defaultValue = "Admission Helpline: +1 (800) 920-4321"
                ),
                EditableField(
                    fieldId = "field_school_website",
                    elementId = "el_edu_website",
                    type = FieldType.WEBSITE,
                    label = "Application Website",
                    defaultValue = "Apply online: www.cambridgeglobal.edu"
                )
            )
        ),

        // 6. Gym & Fitness Transformation (Square)
        Template(
            templateId = "tmpl_gym_fitness",
            creatorId = "creator_design_lab",
            creatorName = "DesignLab Co",
            name = "Hardcore Gym Challenge",
            description = "High energy fitness poster for gym memberships and personal training.",
            categoryId = "cat_fitness",
            categoryName = "Fitness & Gym",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.SOLID,
                color1Hex = "#18181B"
            ),
            isFeatured = false,
            isPro = false,
            usageCount = 890,
            favoriteCount = 260,
            rating = 4.8f,
            status = TemplateStatus.APPROVED,
            elements = listOf(
                CanvasElement(
                    id = "el_gym_tag",
                    type = ElementType.TEXT,
                    text = "30-DAY BODY TRANSFORMATION",
                    fontSizeSp = 13f,
                    fontColorHex = "#EA580C",
                    isBold = true,
                    letterSpacingSp = 2f,
                    xRatio = 0.1f,
                    yRatio = 0.12f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_gym_title",
                    type = ElementType.TEXT,
                    text = "UNLEASH YOUR POWER",
                    fontSizeSp = 30f,
                    fontColorHex = "#FAFAFA",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_gym_title",
                    xRatio = 0.1f,
                    yRatio = 0.20f,
                    widthRatio = 0.8f,
                    heightRatio = 0.10f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_gym_offer_box",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#EA580C",
                    shapeCornerRadiusDp = 12f,
                    xRatio = 0.2f,
                    yRatio = 0.35f,
                    widthRatio = 0.6f,
                    heightRatio = 0.11f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_gym_offer_txt",
                    type = ElementType.TEXT,
                    text = "GET 50% OFF FIRST MONTH\n+ Free Personal Trainer Session",
                    fontSizeSp = 13f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_gym_discount",
                    xRatio = 0.22f,
                    yRatio = 0.37f,
                    widthRatio = 0.56f,
                    heightRatio = 0.07f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_gym_name",
                    type = ElementType.TEXT,
                    text = "IRON TEMPLE FITNESS HUB",
                    fontSizeSp = 16f,
                    fontColorHex = "#E4E4E7",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_gym_name",
                    xRatio = 0.1f,
                    yRatio = 0.58f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_gym_loc",
                    type = ElementType.TEXT,
                    text = "📍 720 Olympic Way, Metro Sports Complex",
                    fontSizeSp = 13f,
                    fontColorHex = "#A1A1AA",
                    isEditableField = true,
                    editableFieldId = "field_gym_loc",
                    xRatio = 0.1f,
                    yRatio = 0.66f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_gym_phone",
                    type = ElementType.TEXT,
                    text = "Join Now: +1 (555) 678-9012 • irontemple.fit",
                    fontSizeSp = 13f,
                    fontColorHex = "#FB923C",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_gym_contact",
                    xRatio = 0.1f,
                    yRatio = 0.76f,
                    widthRatio = 0.8f,
                    heightRatio = 0.05f,
                    layerOrder = 7
                )
            ),
            editableFields = listOf(
                EditableField(
                    fieldId = "field_gym_title",
                    elementId = "el_gym_title",
                    type = FieldType.TEXT,
                    label = "Headline Banner",
                    defaultValue = "UNLEASH YOUR POWER"
                ),
                EditableField(
                    fieldId = "field_gym_discount",
                    elementId = "el_gym_offer_txt",
                    type = FieldType.TEXT,
                    label = "Discount & Perk",
                    defaultValue = "GET 50% OFF FIRST MONTH\n+ Free Personal Trainer Session"
                ),
                EditableField(
                    fieldId = "field_gym_name",
                    elementId = "el_gym_name",
                    type = FieldType.TEXT,
                    label = "Gym / Studio Name",
                    defaultValue = "IRON TEMPLE FITNESS HUB"
                ),
                EditableField(
                    fieldId = "field_gym_loc",
                    elementId = "el_gym_loc",
                    type = FieldType.ADDRESS,
                    label = "Address",
                    defaultValue = "📍 720 Olympic Way, Metro Sports Complex"
                ),
                EditableField(
                    fieldId = "field_gym_contact",
                    elementId = "el_gym_phone",
                    type = FieldType.PHONE,
                    label = "Contact & Website",
                    defaultValue = "Join Now: +1 (555) 678-9012 • irontemple.fit"
                )
            )
        ),

        // 7. Global AI & Tech Summit 2026 (Story 9:16)
        Template(
            templateId = "tmpl_ai_summit_2026",
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "Global AI & Tech Summit 2026",
            description = "Futuristic cyber gradient poster for tech conferences, AI keynotes, and hackathons.",
            categoryId = "cat_events",
            categoryName = "Events",
            canvasWidth = 1080,
            canvasHeight = 1920,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#0B1120",
                color2Hex = "#1E1B4B",
                angleDegrees = 160f
            ),
            isFeatured = true,
            isPro = true,
            usageCount = 2890,
            favoriteCount = 940,
            rating = 5.0f,
            status = TemplateStatus.APPROVED,
            permissionLevel = TemplatePermissionLevel.FULLY_EDITABLE,
            elements = listOf(
                CanvasElement(
                    id = "el_ai_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#3B82F6",
                    shapeCornerRadiusDp = 10f,
                    xRatio = 0.1f,
                    yRatio = 0.08f,
                    widthRatio = 0.45f,
                    heightRatio = 0.035f,
                    layerOrder = 0
                ),
                CanvasElement(
                    id = "el_ai_tag",
                    type = ElementType.TEXT,
                    text = "NEXT-GEN INTELLIGENCE",
                    fontSizeSp = 11f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    letterSpacingSp = 1.5f,
                    xRatio = 0.12f,
                    yRatio = 0.085f,
                    widthRatio = 0.42f,
                    heightRatio = 0.03f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_ai_title",
                    type = ElementType.TEXT,
                    text = "WORLD AI SUMMIT\n& FUTURE EXPO",
                    fontSizeSp = 28f,
                    fontColorHex = "#F8FAFC",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_ai_title",
                    xRatio = 0.1f,
                    yRatio = 0.14f,
                    widthRatio = 0.8f,
                    heightRatio = 0.11f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_ai_subtitle",
                    type = ElementType.TEXT,
                    text = "Featuring 50+ Global Keynote Visionaries, Deep Learning Labs & Startup Pitch Arena",
                    fontSizeSp = 13f,
                    fontColorHex = "#94A3B8",
                    isEditableField = true,
                    editableFieldId = "field_ai_sub",
                    xRatio = 0.1f,
                    yRatio = 0.26f,
                    widthRatio = 0.8f,
                    heightRatio = 0.08f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_ai_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#1E293B",
                    strokeColorHex = "#38BDF8",
                    strokeWidthDp = 1.5f,
                    shapeCornerRadiusDp = 16f,
                    xRatio = 0.1f,
                    yRatio = 0.36f,
                    widthRatio = 0.8f,
                    heightRatio = 0.22f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_ai_date",
                    type = ElementType.TEXT,
                    text = "📅 OCTOBER 14-16, 2026\n📍 Grand Civic Tech Convention Center",
                    fontSizeSp = 14f,
                    fontColorHex = "#38BDF8",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_ai_date",
                    xRatio = 0.15f,
                    yRatio = 0.39f,
                    widthRatio = 0.7f,
                    heightRatio = 0.07f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_ai_speakers",
                    type = ElementType.TEXT,
                    text = "Special Keynotes: Dr. Elena Vance • Marcus Sterling • Lisa Chen",
                    fontSizeSp = 12f,
                    fontColorHex = "#F1F5F9",
                    xRatio = 0.15f,
                    yRatio = 0.47f,
                    widthRatio = 0.7f,
                    heightRatio = 0.08f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_ai_cta_bg",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#38BDF8",
                    shapeCornerRadiusDp = 14f,
                    xRatio = 0.1f,
                    yRatio = 0.62f,
                    widthRatio = 0.8f,
                    heightRatio = 0.06f,
                    layerOrder = 7
                ),
                CanvasElement(
                    id = "el_ai_cta_txt",
                    type = ElementType.TEXT,
                    text = "RESERVE PASSES • AISUMMIT2026.IO",
                    fontSizeSp = 13f,
                    fontColorHex = "#0B1120",
                    isBold = true,
                    xRatio = 0.1f,
                    yRatio = 0.635f,
                    widthRatio = 0.8f,
                    heightRatio = 0.04f,
                    layerOrder = 8
                )
            ),
            editableFields = listOf(
                EditableField("field_ai_title", "el_ai_title", FieldType.TEXT, "Event Title", "WORLD AI SUMMIT\n& FUTURE EXPO"),
                EditableField("field_ai_sub", "el_ai_subtitle", FieldType.TEXT, "Description", "Featuring 50+ Global Keynote Visionaries"),
                EditableField("field_ai_date", "el_ai_date", FieldType.DATE, "Date & Venue", "📅 OCTOBER 14-16, 2026\n📍 Grand Civic Tech Center")
            )
        ),

        // 8. Gourmet Burger & Craft Beer Weekend (Square 1:1)
        Template(
            templateId = "tmpl_burger_fest",
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "Gourmet Burger & Beer Fest",
            description = "Delicious food promotion flyer with discount badge and appetizing layout.",
            categoryId = "cat_food",
            categoryName = "Food & Cafe",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#18181B",
                color2Hex = "#27272A",
                angleDegrees = 145f
            ),
            isFeatured = true,
            isPro = false,
            usageCount = 1840,
            favoriteCount = 620,
            rating = 4.8f,
            status = TemplateStatus.APPROVED,
            permissionLevel = TemplatePermissionLevel.FULLY_EDITABLE,
            elements = listOf(
                CanvasElement(
                    id = "el_burg_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#F59E0B",
                    shapeCornerRadiusDp = 8f,
                    xRatio = 0.1f,
                    yRatio = 0.1f,
                    widthRatio = 0.35f,
                    heightRatio = 0.05f,
                    layerOrder = 0
                ),
                CanvasElement(
                    id = "el_burg_badge_txt",
                    type = ElementType.TEXT,
                    text = "WEEKEND SPECIAL",
                    fontSizeSp = 11f,
                    fontColorHex = "#000000",
                    isBold = true,
                    xRatio = 0.12f,
                    yRatio = 0.11f,
                    widthRatio = 0.31f,
                    heightRatio = 0.04f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_burg_title",
                    type = ElementType.TEXT,
                    text = "SMASH BURGER\n& CRAFT BEER",
                    fontSizeSp = 26f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_burg_title",
                    xRatio = 0.1f,
                    yRatio = 0.18f,
                    widthRatio = 0.8f,
                    heightRatio = 0.16f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_burg_offer_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#EF4444",
                    shapeCornerRadiusDp = 16f,
                    xRatio = 0.1f,
                    yRatio = 0.37f,
                    widthRatio = 0.45f,
                    heightRatio = 0.15f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_burg_offer_txt",
                    type = ElementType.TEXT,
                    text = "BUY 1 GET 1\nFREE BEER 🍺",
                    fontSizeSp = 16f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_burg_offer",
                    xRatio = 0.13f,
                    yRatio = 0.40f,
                    widthRatio = 0.39f,
                    heightRatio = 0.10f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_burg_details",
                    type = ElementType.TEXT,
                    text = "Fresh 100% Angus Beef • Brioche Bun • Hand-cut Truffle Fries • IPA Selection",
                    fontSizeSp = 12f,
                    fontColorHex = "#A1A1AA",
                    xRatio = 0.1f,
                    yRatio = 0.56f,
                    widthRatio = 0.8f,
                    heightRatio = 0.1f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_burg_loc",
                    type = ElementType.TEXT,
                    text = "📍 404 Downtown Avenue • Open Daily 12PM - 11PM\nCall for Reservations: (555) 890-1234",
                    fontSizeSp = 12f,
                    fontColorHex = "#FBBF24",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_burg_loc",
                    xRatio = 0.1f,
                    yRatio = 0.70f,
                    widthRatio = 0.8f,
                    heightRatio = 0.1f,
                    layerOrder = 6
                )
            ),
            editableFields = listOf(
                EditableField("field_burg_title", "el_burg_title", FieldType.TEXT, "Main Dish / Combo", "SMASH BURGER\n& CRAFT BEER"),
                EditableField("field_burg_offer", "el_burg_offer_txt", FieldType.TEXT, "Special Promo", "BUY 1 GET 1\nFREE BEER 🍺"),
                EditableField("field_burg_loc", "el_burg_loc", FieldType.ADDRESS, "Location & Hours", "📍 404 Downtown Avenue • Open Daily")
            )
        ),

        // 9. Cyber Weekend Mega Flash Sale (Square 1:1)
        Template(
            templateId = "tmpl_flash_sale_cyber",
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "Cyber Mega Flash Sale",
            description = "High energy retail sale banner with vibrant discount tags and promo codes.",
            categoryId = "cat_offers",
            categoryName = "Special Offers",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_LINEAR,
                color1Hex = "#4C0519",
                color2Hex = "#0F172A",
                angleDegrees = 135f
            ),
            isFeatured = true,
            isPro = true,
            usageCount = 3120,
            favoriteCount = 890,
            rating = 4.9f,
            status = TemplateStatus.APPROVED,
            permissionLevel = TemplatePermissionLevel.FULLY_EDITABLE,
            elements = listOf(
                CanvasElement(
                    id = "el_sale_pill",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#F43F5E",
                    shapeCornerRadiusDp = 12f,
                    xRatio = 0.1f,
                    yRatio = 0.1f,
                    widthRatio = 0.4f,
                    heightRatio = 0.05f,
                    layerOrder = 0
                ),
                CanvasElement(
                    id = "el_sale_pill_txt",
                    type = ElementType.TEXT,
                    text = "⚡ LIMITED 48H ONLY",
                    fontSizeSp = 12f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    xRatio = 0.12f,
                    yRatio = 0.11f,
                    widthRatio = 0.36f,
                    heightRatio = 0.04f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_sale_title",
                    type = ElementType.TEXT,
                    text = "MEGA FLASH\nDISCOUNT",
                    fontSizeSp = 30f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_sale_title",
                    xRatio = 0.1f,
                    yRatio = 0.18f,
                    widthRatio = 0.8f,
                    heightRatio = 0.16f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_sale_disc_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#FBBF24",
                    shapeCornerRadiusDp = 18f,
                    xRatio = 0.1f,
                    yRatio = 0.36f,
                    widthRatio = 0.55f,
                    heightRatio = 0.18f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_sale_disc_txt",
                    type = ElementType.TEXT,
                    text = "UP TO 70% OFF\nSTOREWIDE",
                    fontSizeSp = 20f,
                    fontColorHex = "#0F172A",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_sale_disc",
                    xRatio = 0.13f,
                    yRatio = 0.39f,
                    widthRatio = 0.49f,
                    heightRatio = 0.12f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_sale_code_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#1E293B",
                    strokeColorHex = "#F43F5E",
                    strokeWidthDp = 2f,
                    shapeCornerRadiusDp = 10f,
                    xRatio = 0.1f,
                    yRatio = 0.58f,
                    widthRatio = 0.8f,
                    heightRatio = 0.08f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_sale_code_txt",
                    type = ElementType.TEXT,
                    text = "USE PROMO CODE: FLASH2026 AT CHECKOUT",
                    fontSizeSp = 13f,
                    fontColorHex = "#F8FAFC",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_sale_code",
                    xRatio = 0.12f,
                    yRatio = 0.60f,
                    widthRatio = 0.76f,
                    heightRatio = 0.05f,
                    layerOrder = 6
                ),
                CanvasElement(
                    id = "el_sale_cta",
                    type = ElementType.TEXT,
                    text = "Shop Online at: www.brandstore.com • Free Express Shipping",
                    fontSizeSp = 12f,
                    fontColorHex = "#94A3B8",
                    xRatio = 0.1f,
                    yRatio = 0.70f,
                    widthRatio = 0.8f,
                    heightRatio = 0.06f,
                    layerOrder = 7
                )
            ),
            editableFields = listOf(
                EditableField("field_sale_title", "el_sale_title", FieldType.TEXT, "Sale Headline", "MEGA FLASH\nDISCOUNT"),
                EditableField("field_sale_disc", "el_sale_disc_txt", FieldType.TEXT, "Discount Amount", "UP TO 70% OFF\nSTOREWIDE"),
                EditableField("field_sale_code", "el_sale_code_txt", FieldType.TEXT, "Voucher / Code", "USE PROMO CODE: FLASH2026")
            )
        ),

        // 10. Artisan Coffee & Bakery Grand Opening (Square 1:1)
        Template(
            templateId = "tmpl_coffee_roasters",
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "Artisan Coffee & Bakery Opening",
            description = "Warm cafe poster with aesthetic typography and discount offer for foodies.",
            categoryId = "cat_food",
            categoryName = "Food & Cafe",
            canvasWidth = 1080,
            canvasHeight = 1080,
            background = PosterBackground(
                type = BackgroundType.SOLID,
                color1Hex = "#1C1917"
            ),
            isFeatured = false,
            isPro = false,
            usageCount = 1420,
            favoriteCount = 490,
            rating = 4.9f,
            status = TemplateStatus.APPROVED,
            permissionLevel = TemplatePermissionLevel.FULLY_EDITABLE,
            elements = listOf(
                CanvasElement(
                    id = "el_cof_bg_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#292524",
                    strokeColorHex = "#78350F",
                    strokeWidthDp = 1.5f,
                    shapeCornerRadiusDp = 20f,
                    xRatio = 0.08f,
                    yRatio = 0.08f,
                    widthRatio = 0.84f,
                    heightRatio = 0.84f,
                    layerOrder = 0
                ),
                CanvasElement(
                    id = "el_cof_tag",
                    type = ElementType.TEXT,
                    text = "☕ SPECIALTY ROASTERS & PASTRY",
                    fontSizeSp = 11f,
                    fontColorHex = "#D97706",
                    isBold = true,
                    letterSpacingSp = 1.5f,
                    xRatio = 0.15f,
                    yRatio = 0.15f,
                    widthRatio = 0.7f,
                    heightRatio = 0.05f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_cof_title",
                    type = ElementType.TEXT,
                    text = "VELVET BEAN\nCAFE & ROASTERY",
                    fontSizeSp = 24f,
                    fontColorHex = "#FEF3C7",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_cof_title",
                    xRatio = 0.15f,
                    yRatio = 0.22f,
                    widthRatio = 0.7f,
                    heightRatio = 0.14f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_cof_desc",
                    type = ElementType.TEXT,
                    text = "Join us for our Grand Opening! Enjoy artisanal single-origin pour-overs, sourdough croissants, and gourmet matcha lattes.",
                    fontSizeSp = 13f,
                    fontColorHex = "#A8A29E",
                    isEditableField = true,
                    editableFieldId = "field_cof_desc",
                    xRatio = 0.15f,
                    yRatio = 0.38f,
                    widthRatio = 0.7f,
                    heightRatio = 0.14f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_cof_badge",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#D97706",
                    shapeCornerRadiusDp = 10f,
                    xRatio = 0.15f,
                    yRatio = 0.54f,
                    widthRatio = 0.7f,
                    heightRatio = 0.07f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_cof_badge_txt",
                    type = ElementType.TEXT,
                    text = "FREE CROISSANT WITH ANY COFFEE 🥐",
                    fontSizeSp = 12f,
                    fontColorHex = "#1C1917",
                    isBold = true,
                    xRatio = 0.15f,
                    yRatio = 0.555f,
                    widthRatio = 0.7f,
                    heightRatio = 0.05f,
                    layerOrder = 5
                ),
                CanvasElement(
                    id = "el_cof_loc",
                    type = ElementType.TEXT,
                    text = "📍 128 Maple Boulevard • Mon-Sun 7AM-8PM • @velvetbeancafe",
                    fontSizeSp = 11f,
                    fontColorHex = "#D6D3D1",
                    isEditableField = true,
                    editableFieldId = "field_cof_loc",
                    xRatio = 0.15f,
                    yRatio = 0.65f,
                    widthRatio = 0.7f,
                    heightRatio = 0.08f,
                    layerOrder = 6
                )
            ),
            editableFields = listOf(
                EditableField("field_cof_title", "el_cof_title", FieldType.TEXT, "Cafe / Brand Name", "VELVET BEAN\nCAFE & ROASTERY"),
                EditableField("field_cof_desc", "el_cof_desc", FieldType.TEXT, "Opening Description", "Join us for our Grand Opening!"),
                EditableField("field_cof_loc", "el_cof_loc", FieldType.ADDRESS, "Address & Handle", "📍 128 Maple Boulevard")
            )
        ),

        // 11. VIP Golden Birthday Celebration (Story 9:16)
        Template(
            templateId = "tmpl_vip_birthday",
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "VIP Golden Birthday Bash",
            description = "Luxurious gold glitter and dark aesthetic celebration invitation poster.",
            categoryId = "cat_birthday",
            categoryName = "Birthday",
            canvasWidth = 1080,
            canvasHeight = 1920,
            background = PosterBackground(
                type = BackgroundType.GRADIENT_RADIAL,
                color1Hex = "#2E1065",
                color2Hex = "#0F172A"
            ),
            isFeatured = false,
            isPro = true,
            usageCount = 2200,
            favoriteCount = 730,
            rating = 4.9f,
            status = TemplateStatus.APPROVED,
            permissionLevel = TemplatePermissionLevel.FULLY_EDITABLE,
            elements = listOf(
                CanvasElement(
                    id = "el_bday_tag",
                    type = ElementType.TEXT,
                    text = "✨ YOU ARE CORDIALLY INVITED TO ✨",
                    fontSizeSp = 12f,
                    fontColorHex = "#FBBF24",
                    isBold = true,
                    letterSpacingSp = 1.5f,
                    xRatio = 0.1f,
                    yRatio = 0.12f,
                    widthRatio = 0.8f,
                    heightRatio = 0.04f,
                    layerOrder = 0
                ),
                CanvasElement(
                    id = "el_bday_title",
                    type = ElementType.TEXT,
                    text = "SOPHIA'S 25TH\nGOLDEN BASH",
                    fontSizeSp = 28f,
                    fontColorHex = "#FFFFFF",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_bday_name",
                    xRatio = 0.1f,
                    yRatio = 0.18f,
                    widthRatio = 0.8f,
                    heightRatio = 0.12f,
                    layerOrder = 1
                ),
                CanvasElement(
                    id = "el_bday_card",
                    type = ElementType.SHAPE,
                    shapeType = ShapeType.ROUNDED_RECT,
                    fillColorHex = "#1E1B4B",
                    strokeColorHex = "#A855F7",
                    strokeWidthDp = 1.5f,
                    shapeCornerRadiusDp = 18f,
                    xRatio = 0.1f,
                    yRatio = 0.32f,
                    widthRatio = 0.8f,
                    heightRatio = 0.28f,
                    layerOrder = 2
                ),
                CanvasElement(
                    id = "el_bday_date",
                    type = ElementType.TEXT,
                    text = "🍸 SATURDAY, NOVEMBER 21\nDoors Open at 8:00 PM",
                    fontSizeSp = 15f,
                    fontColorHex = "#FBBF24",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_bday_date",
                    xRatio = 0.15f,
                    yRatio = 0.36f,
                    widthRatio = 0.7f,
                    heightRatio = 0.08f,
                    layerOrder = 3
                ),
                CanvasElement(
                    id = "el_bday_loc",
                    type = ElementType.TEXT,
                    text = "📍 The Skyline Penthouse Lounge\n88 Infinity Tower, 45th Floor",
                    fontSizeSp = 13f,
                    fontColorHex = "#E2E8F0",
                    isEditableField = true,
                    editableFieldId = "field_bday_loc",
                    xRatio = 0.15f,
                    yRatio = 0.46f,
                    widthRatio = 0.7f,
                    heightRatio = 0.08f,
                    layerOrder = 4
                ),
                CanvasElement(
                    id = "el_bday_rsvp",
                    type = ElementType.TEXT,
                    text = "Dress Code: Elegant Black & Gold • RSVP to (555) 987-6543",
                    fontSizeSp = 12f,
                    fontColorHex = "#C084FC",
                    isBold = true,
                    isEditableField = true,
                    editableFieldId = "field_bday_rsvp",
                    xRatio = 0.1f,
                    yRatio = 0.63f,
                    widthRatio = 0.8f,
                    heightRatio = 0.06f,
                    layerOrder = 5
                )
            ),
            editableFields = listOf(
                EditableField("field_bday_name", "el_bday_title", FieldType.TEXT, "Celebrant & Event", "SOPHIA'S 25TH\nGOLDEN BASH"),
                EditableField("field_bday_date", "el_bday_date", FieldType.DATE, "Date & Time", "🍸 SATURDAY, NOVEMBER 21"),
                EditableField("field_bday_loc", "el_bday_loc", FieldType.ADDRESS, "Venue Location", "📍 The Skyline Penthouse Lounge"),
                EditableField("field_bday_rsvp", "el_bday_rsvp", FieldType.PHONE, "RSVP & Dress Code", "RSVP to (555) 987-6543")
            )
        )
    )

    fun getInitialUsers(): List<com.example.domain.models.UserProfile> = listOf(
        com.example.domain.models.UserProfile(
            id = "user_demo",
            name = "Alex Rivera",
            email = "alex.rivera@example.com",
            role = com.example.domain.models.UserRole.USER,
            bio = "Marketing specialist & content creator",
            totalTemplatesCreated = 0,
            totalDownloads = 12
        ),
        com.example.domain.models.UserProfile(
            id = "creator_studio_x",
            name = "Studio X Designs",
            email = "creator@studiox.io",
            role = com.example.domain.models.UserRole.CREATOR,
            isCreatorVerified = true,
            bio = "Official visual branding studio creating responsive poster templates",
            totalTemplatesCreated = 18,
            totalDownloads = 4850
        ),
        com.example.domain.models.UserProfile(
            id = "admin_master",
            name = "System Admin",
            email = "admin@postermaker.com",
            role = com.example.domain.models.UserRole.ADMIN,
            bio = "Platform operations & content moderation manager",
            totalTemplatesCreated = 4,
            totalDownloads = 950
        )
    )
}
