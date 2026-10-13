package com.govguide.service;

import com.govguide.dto.SchemeDto;
import com.govguide.dto.VoiceQueryRequest;
import com.govguide.dto.VoiceQueryResponse;
import com.govguide.entity.Scheme;
import com.govguide.repository.SchemeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoiceAssistantService {

    private final SchemeRepository schemeRepository;
    private final SchemeService schemeService;

    public VoiceAssistantService(SchemeRepository schemeRepository, SchemeService schemeService) {
        this.schemeRepository = schemeRepository;
        this.schemeService = schemeService;
    }

    public VoiceQueryResponse processVoiceQuery(VoiceQueryRequest request) {
        String query = request.getQuery() != null ? request.getQuery().trim().toLowerCase() : "";
        String lang = request.getLanguage() != null ? request.getLanguage().toLowerCase() : "en";

        // Auto-detect script if telugu or devanagari characters detected
        if (containsTelugu(query)) {
            lang = "te";
        } else if (containsHindi(query)) {
            lang = "hi";
        }

        List<Scheme> allSchemes = schemeRepository.findByPublicationStatus("PUBLISHED");
        List<Scheme> matchedSchemes = new ArrayList<>();
        String spokenResponse;
        String displayResponse;
        String intent = "GENERAL_SEARCH";
        List<String> followUps = new ArrayList<>();

        // Intent 1: Farmer / Agriculture schemes
        if (matchesAny(query, "farmer", "agriculture", "kisan", "రైతు", "వ్యవసాయం", "రైతులకు", "किसान", "खेती", "कृषि")) {
            intent = "FARMER_SCHEMES";
            matchedSchemes = allSchemes.stream()
                    .filter(s -> "Agriculture".equalsIgnoreCase(s.getCategory()) ||
                            s.getSchemeName().toLowerCase().contains("kisan") ||
                            s.getSchemeName().toLowerCase().contains("rythu"))
                    .collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "రైతుల కోసం ప్రధాన మంత్రి కిసాన్ సమ్మాన్ నిధి మరియు వైఎస్సార్ రైతు భరోసా పథకాలు అందుబాటులో ఉన్నాయి. వీటి ద్వారా రైతులకు పెట్టుబడి సహాయం అందుతుంది.";
                displayResponse = "🌾 **రైతు సంక్షేమ పథకాలు:**\n1. **పిఎం-కిసాన్ (PM-KISAN)**: సంవత్సరానికి ₹6,000 పెట్టుబడి సహాయం.\n2. **వైఎస్సార్ రైతు భరోసా**: ఆంధ్రప్రదేశ్ రైతులకు సంవత్సరానికి ₹13,500 పెట్టుబడి సహాయం.";
                followUps.add("ఈ పథకాలకు ఏ పత్రాలు కావాలి?");
                followUps.add("దరఖాస్తు ఎలా చేసుకోవాలి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "किसानों के लिए प्रधानमंत्री किसान सम्मान निधि योजना उपलब्ध है, जिसके तहत सालाना छह हजार रुपये की आर्थिक सहायता मिलती है।";
                displayResponse = "🌾 **किसानों के लिए प्रमुख योजनाएं:**\n1. **पीएम-किसान (PM-KISAN)**: प्रति वर्ष ₹6,000 की प्रत्यक्ष सहायता।\n2. **राज्य कृषि योजनाएं**: बीज, खाद और उपकरण पर सब्सिडी।";
                followUps.add("पीएम-किसान के लिए कौन से दस्तावेज़ चाहिए?");
                followUps.add("आवेदन कैसे करें?");
            } else {
                spokenResponse = "Here are the top schemes for farmers: PM-KISAN provides ₹6,000 per year, and state schemes like Rythu Bharosa offer financial support for cultivation.";
                displayResponse = "🌾 **Key Schemes for Farmers:**\n1. **PM-KISAN**: ₹6,000 per year direct income support.\n2. **Rythu Bharosa / Rythu Bandhu**: State agricultural investment assistance.";
                followUps.add("What documents are required for PM-KISAN?");
                followUps.add("How do I apply on the official portal?");
            }
        }
        // Intent 2: Healthcare / Hospital schemes
        else if (matchesAny(query, "health", "hospital", "ayushman", "treatment", "వైద్యం", "ఆరోగ్య", "ఆసుపత్రి", "स्वास्थ्य", "इलाज", "अस्पताल", "आयुष्मान")) {
            intent = "HEALTH_SCHEMES";
            matchedSchemes = allSchemes.stream()
                    .filter(s -> "Healthcare".equalsIgnoreCase(s.getCategory()) || s.getSchemeName().toLowerCase().contains("ayushman"))
                    .collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "ఆరోగ్య రక్షణ కోసం ఆయుష్మాన్ భారత్ పిఎం-జేవై పథకం ఉంది. దీని ద్వారా కుటుంబానికి సంవత్సరానికి ఐదు లక్షల రూపాయల వరకు ఉచిత ఆసుపత్రి చికిత్స లభిస్తుంది.";
                displayResponse = "🏥 **ఆరోగ్య పథకాలు:**\n- **ఆయుష్మాన్ భారత్ (AB-PMJAY)**: ప్రతి కుటుంబానికి సంవత్సరానికి ₹5 లక్షల వరకు ఉచిత చికిత్స నగదు రహితంగా లభిస్తుంది.";
                followUps.add("ఆయుష్మాన్ కార్డు ఎలా పొందాలి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "स्वास्थ्य के लिए आयुष्मान भारत योजना है, जिसमें प्रति परिवार हर साल पांच लाख रुपये तक का मुफ्त इलाज मिलता है।";
                displayResponse = "🏥 **स्वास्थ्य योजनाएं:**\n- **आयुष्मान भारत (AB-PMJAY)**: सालाना ₹5 लाख तक का कैशलेस अस्पताल इलाज।";
                followUps.add("आयुष्मान कार्ड कैसे बनवाएं?");
            } else {
                spokenResponse = "For healthcare, Ayushman Bharat PM-JAY provides up to ₹5 Lakh cashless health cover per family per year.";
                displayResponse = "🏥 **Healthcare Schemes:**\n- **Ayushman Bharat (AB-PMJAY)**: Up to ₹5 Lakh per family per year for hospitalization across empanelled hospitals.";
                followUps.add("How do I get an Ayushman Golden Card?");
            }
        }
        // Intent 3: Housing / House schemes
        else if (matchesAny(query, "house", "housing", "awas", "ఇల్లు", "గృహ", "మకాన్", "आवास", "घर")) {
            intent = "HOUSING_SCHEMES";
            matchedSchemes = allSchemes.stream()
                    .filter(s -> "Housing".equalsIgnoreCase(s.getCategory()) || s.getSchemeName().toLowerCase().contains("awas"))
                    .collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "సొంత ఇల్లు నిర్మించుకోవడానికి ప్రధాన మంత్రి ఆవాస్ యోజన ద్వారా రెండు లక్షల అరవై ఏడు వేల రూపాయల వరకు సబ్సిడీ లభిస్తుంది.";
                displayResponse = "🏠 **గృహ నిర్మాణ పథకాలు:**\n- **ప్రధాన మంత్రి ఆవాస్ యోజన (PMAY)**: పక్కా ఇల్లు లేని పేద కుటుంబాలకు ఆర్థిక రాయితీ మరియు గృహ రుణ సబ్సిడీ.";
                followUps.add("ఆవాస్ యోజన అర్హతలు ఏమిటి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "घर बनाने के लिए प्रधानमंत्री आवास योजना उपलब्ध है, जिसके तहत आर्थिक सहायता और होम लोन सब्सिडी मिलती है।";
                displayResponse = "🏠 **आवास योजनाएं:**\n- **प्रधानमंत्री आवास योजना (PMAY)**: पक्का मकान बनाने के लिए ₹2.67 लाख तक की सब्सिडी।";
                followUps.add("आवास योजना की पात्रता क्या है?");
            } else {
                spokenResponse = "For housing, Pradhan Mantri Awas Yojana provides financial grants and home loan interest subsidies up to ₹2.67 Lakh.";
                displayResponse = "🏠 **Housing Schemes:**\n- **Pradhan Mantri Awas Yojana (PMAY)**: Financial assistance and interest subsidy for pucca house construction.";
                followUps.add("Who is eligible for PMAY housing?");
            }
        }
        // Intent 4: Student / Education / Scholarship
        else if (matchesAny(query, "student", "education", "scholarship", "college", "విద్యార్థి", "చదువు", "స్కాలర్‌షిప్", "విద్య", "छात्र", "पढ़ाई", "छात्रवृत्ति", "स्कॉलरशिप")) {
            intent = "EDUCATION_SCHEMES";
            matchedSchemes = allSchemes.stream()
                    .filter(s -> "Education".equalsIgnoreCase(s.getCategory()) || s.getSchemeName().toLowerCase().contains("scholarship"))
                    .collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "విద్యార్థుల కోసం పోస్ట్-మెట్రిక్ స్కాలర్‌షిప్ పథకం ఉంది. దీని ద్వారా పూర్తి కాలేజీ ఫీజు రీయింబర్స్‌మెంట్ మరియు నెలవారీ ఉపకార వేతనం లభిస్తుంది.";
                displayResponse = "🎓 **విద్యా పథకాలు:**\n- **పోస్ట్-మెట్రిక్ స్కాలర్‌షిప్**: ఎస్సీ, ఎస్టీ, బీసీ విద్యార్థులకు 100% కాలేజీ ఫీజు చెల్లింపు మరియు భత్యం.";
                followUps.add("నేషనల్ స్కాలర్‌షిప్ పోర్టల్‌లో ఎలా నమోదు చేసుకోవాలి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "विद्यार्थियों के लिए पोस्ट-मैट्रिक छात्रवृत्ति योजना उपलब्ध है, जिसमें पूरी कॉलेज फीस की प्रतिपूर्ति और मासिक भत्ता मिलता है।";
                displayResponse = "🎓 **शिक्षा एवं छात्रवृत्ति योजनाएं:**\n- **पोस्ट-मैट्रिक छात्रवृत्ति**: एससी, एसटी, ओबीसी छात्रों को उच्च शिक्षा हेतु पूर्ण शुल्क प्रतिपूर्ति।";
                followUps.add("नेशनल स्कॉलरशिप पोर्टल पर आवेदन कैसे करें?");
            } else {
                spokenResponse = "For students, the Post-Matric Scholarship provides 100% tuition fee reimbursement and monthly maintenance allowances for higher education.";
                displayResponse = "🎓 **Education & Scholarships:**\n- **Post-Matric Scholarship Scheme**: Full fee reimbursement and maintenance allowance on scholarships.gov.in.";
                followUps.add("What documents are required for Post-Matric Scholarship?");
            }
        }
        // Intent 5: Required documents inquiry
        else if (matchesAny(query, "document", "documents", "paper", "పత్రాలు", "దస్తావేజులు", "కావాలి", "दस्तावेज़", "कागजात", "प्रमाण पत्र")) {
            intent = "DOCUMENTS_INFO";
            matchedSchemes = allSchemes.stream().limit(3).collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "ప్రభుత్వ పథకాలకు సాధారణంగా ఆధార్ కార్డు, బ్యాంక్ పాస్ బుక్, ఆదాయ ధృవీకరణ పత్రం, మరియు రేషన్ కార్డు అవసరం అవుతాయి.";
                displayResponse = "📄 **సాధారణంగా అవసరమైన పత్రాలు:**\n1. ఆధార్ కార్డు (మొబైల్ లింక్ ఉన్నది)\n2. బ్యాంక్ ఖాతా పాస్‌బుక్ (Aadhaar Seeded)\n3. ఆదాయ ధృవీకరణ పత్రం\n4. కుల ధృవీకరణ పత్రం (అవసరమైన పథకాలకు)\n5. తెల్ల రేషన్ కార్డు / ఆహార భద్రత కార్డు";
                followUps.add("నా అర్హతను ఎలా తనిఖీ చేయాలి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "सरकारी योजनाओं के लिए आमतौर पर आधार कार्ड, बैंक पासबुक, आय प्रमाण पत्र और राशन कार्ड की आवश्यकता होती है।";
                displayResponse = "📄 **आवश्यक दस्तावेज़:**\n1. आधार कार्ड (मोबाइल से लिंक)\n2. बैंक पासबुक (डीबीटी सक्षम)\n3. आय प्रमाण पत्र\n4. जाति प्रमाण पत्र (यदि लागू हो)\n5. राशन कार्ड";
                followUps.add("मेरी पात्रता कैसे जांचें?");
            } else {
                spokenResponse = "Most government welfare schemes typically require your Aadhaar Card, Aadhaar-linked Bank Passbook, Income Certificate, and Ration Card.";
                displayResponse = "📄 **Commonly Required Documents:**\n1. Aadhaar Card (linked to mobile number)\n2. Bank Account Passbook (Aadhaar DBT enabled)\n3. Income Certificate from Revenue Authority\n4. Caste Certificate (if applying under SC/ST/OBC criteria)\n5. Ration Card / Food Security Card";
                followUps.add("How do I check my eligibility?");
            }
        }
        // Intent 6: General eligibility question / Greeting
        else {
            intent = "GENERAL_GUIDANCE";
            matchedSchemes = allSchemes.stream().limit(4).collect(Collectors.toList());

            if ("te".equals(lang)) {
                spokenResponse = "నమస్కారం! ప్రభుత్వ పథకాల సమాచారం కోసం నేను సహాయం చేయగలను. మీ వయస్సు, వృత్తి, లేదా ఆదాయాన్ని తెలపండి లేదా అర్హత తనిఖీ బటన్ నొక్కండి.";
                displayResponse = "🙏 **గోవ్‌గైడ్ వాయిస్ అసిస్టెంట్:**\nమీకు ఏ పథకాలు అర్హత ఉన్నాయో తెలుసుకోవడానికి 'అర్హత తనిఖీ' ఫారమ్ నింపండి లేదా రైతు, విద్యార్థి, ఆరోగ్యం వంటి పథకాల గురించి అడగండి.";
                followUps.add("రైతులకు ఏ పథకాలు ఉన్నాయి?");
                followUps.add("ఆరోగ్య పథకాలు ఏమిటి?");
            } else if ("hi".equals(lang)) {
                spokenResponse = "नमस्ते! मैं सरकारी योजनाओं की जानकारी देने के लिए तैयार हूँ। आप अपनी उम्र, व्यवसाय या आय बताकर या पात्रता जांच बटन पर क्लिक करके योजनाएं खोज सकते हैं।";
                displayResponse = "🙏 **गवगाइड वॉइस असिस्टेंट:**\nसरकारी योजनाओं की खोज के लिए आप किसानों, स्वास्थ्य, आवास, या छात्रवृत्ति के बारे में पूछ सकते हैं।";
                followUps.add("किसानों के लिए कौन सी योजनाएँ हैं?");
                followUps.add("स्वास्थ्य योजनाएं क्या हैं?");
            } else {
                spokenResponse = "Hello! GovGuide helps you discover official government schemes. You can ask for schemes by occupation, like farmers or students, or click Check Eligibility to view all schemes matched to your profile.";
                displayResponse = "🙏 **GovGuide Voice Assistant:**\nAsk me about schemes for farmers, healthcare, housing, scholarships, or micro-business loans. You can also use the full Eligibility Checker.";
                followUps.add("Show me schemes for farmers");
                followUps.add("What healthcare schemes are available?");
            }
        }

        VoiceQueryResponse response = new VoiceQueryResponse();
        response.setSpokenResponse(spokenResponse);
        response.setDisplayResponse(displayResponse);
        response.setIntent(intent);
        response.setDetectedLanguage(lang);
        response.setSuggestedFollowUps(followUps);

        List<SchemeDto> dtos = matchedSchemes.stream()
                .map(s -> schemeService.mapToDto(s, null))
                .collect(Collectors.toList());
        response.setMatchedSchemes(dtos);

        return response;
    }

    private boolean containsTelugu(String text) {
        for (char c : text.toCharArray()) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
            if (block == Character.UnicodeBlock.TELUGU) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHindi(String text) {
        for (char c : text.toCharArray()) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
            if (block == Character.UnicodeBlock.DEVANAGARI) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
