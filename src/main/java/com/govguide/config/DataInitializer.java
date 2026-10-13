package com.govguide.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.govguide.entity.Scheme;
import com.govguide.entity.SchemeDocument;
import com.govguide.entity.SchemeEligibilityRule;
import com.govguide.entity.User;
import com.govguide.repository.SchemeRepository;
import com.govguide.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SchemeRepository schemeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${govguide.admin.default-email:admin@govguide.nic.in}")
    private String adminEmail;
@Value("${govguide.admin.default-password}")
private String adminPassword;
    
    @Value("${govguide.admin.default-name:GovGuide Administrator}")
    private String adminName;

    public DataInitializer(UserRepository userRepository, SchemeRepository schemeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.schemeRepository = schemeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initAdminUser();
        initSchemes();
    }

    private void initAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail);
            admin.setMobileNumber("9876543210");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole("ROLE_ADMIN");
            admin.setPreferredLanguage("en");
            admin.setAccountStatus("ACTIVE");
            userRepository.save(admin);
            log.info("Initialized default administrator account: {}", adminEmail);
        }
    }

    private void initSchemes() {
        if (schemeRepository.count() > 0) {
            return;
        }

        log.info("Seeding verified Indian government welfare schemes into database...");

        // 1. PM-KISAN
        Scheme pmkisan = new Scheme();
        pmkisan.setSchemeName("Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)");
        pmkisan.setSchemeCode("PM-KISAN");
        pmkisan.setCategory("Agriculture");
        pmkisan.setGovernmentLevel("CENTRAL");
        pmkisan.setDepartment("Ministry of Agriculture & Farmers Welfare");
        pmkisan.setShortDescription("Direct income support of ₹6,000 per year in 3 equal installments to landholding farmer families across India.");
        pmkisan.setDetailedDescription("Pradhan Mantri Kisan Samman Nidhi is a Central Sector Scheme providing income support to all landholding farmer families in the country to supplement their financial needs for procuring agricultural inputs and domestic needs.");
        pmkisan.setWhyExists("To augment the income of small and marginal farmers and address their cash flow needs prior to agricultural sowing seasons.");
        pmkisan.setWhoCanBenefit("Small and marginal farmer families having cultivable landholding in their names across all States and Union Territories.");
        pmkisan.setBenefits("Financial benefit of ₹6,000 per annum per family, payable in three equal four-monthly installments of ₹2,000 each transferred via DBT.");
        pmkisan.setApplicationProcess("Step 1: Visit official PM-KISAN portal (pmkisan.gov.in).\nStep 2: Click on 'Farmers Corner' -> 'New Farmer Registration'.\nStep 3: Enter Aadhaar Number and select State.\nStep 4: Fill in personal details, bank account info, and landholding record details (Khata / Khasra number).\nStep 5: Complete e-KYC using Aadhaar OTP.\nStep 6: Submit application and track status via Aadhaar.");
        pmkisan.setOfficialUrl("https://pmkisan.gov.in");
        pmkisan.setVerificationStatus("VERIFIED");
        pmkisan.setPublicationStatus("PUBLISHED");
        pmkisan.setApplicationStatus("OPEN");
        pmkisan.setLastVerifiedAt(LocalDate.of(2026, 8, 1));
        pmkisan.setInformationSource("Official Portal pmkisan.gov.in / Ministry of Agriculture");

        SchemeEligibilityRule pmkisanRule = new SchemeEligibilityRule();
        pmkisanRule.setMinimumAge(18);
        pmkisanRule.setMaximumAge(100);
        pmkisanRule.setMinimumIncome(BigDecimal.ZERO);
        pmkisanRule.setMaximumIncome(new BigDecimal("1000000"));
        pmkisanRule.setEligibleStates("ALL");
        pmkisanRule.setEligibleOccupations("Farmer,Agricultural labourer");
        pmkisanRule.setEligibleSocialCategories("ALL");
        pmkisanRule.setEligibleEducationLevels("ALL");
        pmkisanRule.setGenderRequirement("ALL");
        pmkisanRule.setDisabilityStatus("ALL");
        pmkisanRule.setAdditionalConditions("Must own cultivable agricultural land registered in applicant name. Institutional landholders and income tax payers are excluded.");
        pmkisan.setEligibilityRule(pmkisanRule);

        pmkisan.addDocument(new SchemeDocument("Aadhaar Card", "Mandatory for identification and DBT transfer", true));
        pmkisan.addDocument(new SchemeDocument("Land Ownership Record (Pattadar Passbook / ROR / Khasra)", "Proof of cultivable agricultural land ownership", true));
        pmkisan.addDocument(new SchemeDocument("Bank Passbook / Account Details", "Aadhaar-seeded bank account for direct benefit transfer", true));
        pmkisan.addDocument(new SchemeDocument("Active Mobile Number", "Required for Aadhaar OTP verification and SMS alerts", true));
        schemeRepository.save(pmkisan);

        // 2. Ayushman Bharat PM-JAY
        Scheme pmjay = new Scheme();
        pmjay.setSchemeName("Ayushman Bharat Pradhan Mantri Jan Arogya Yojana (AB-PMJAY)");
        pmjay.setSchemeCode("AB-PMJAY");
        pmjay.setCategory("Healthcare");
        pmjay.setGovernmentLevel("CENTRAL");
        pmjay.setDepartment("National Health Authority (NHA), Ministry of Health and Family Welfare");
        pmjay.setShortDescription("World's largest government-funded health assurance scheme providing up to ₹5 Lakh per family per year for secondary and tertiary hospital care.");
        pmjay.setDetailedDescription("Ayushman Bharat PM-JAY provides health cover of ₹5,00,000 per family per year for secondary and tertiary healthcare hospitalizations across public and private empanelled hospitals in India. Covers pre-existing conditions from day one with cashless service at point of delivery.");
        pmjay.setWhyExists("To prevent catastrophic out-of-pocket health expenditures that push millions of vulnerable Indian families into poverty each year.");
        pmjay.setWhoCanBenefit("Low-income and vulnerable families identified via Socio-Economic Caste Census (SECC 2011) and eligible state ration card holders, daily wage earners, informal sector workers.");
        pmjay.setBenefits("Cashless and paperless access to healthcare services up to ₹5,00,000 per family per year. Covers 3 days pre-hospitalization, 15 days post-hospitalization, medicines, diagnostics, and ICU charges.");
        pmjay.setApplicationProcess("Step 1: Check eligibility at 'Am I Eligible' portal on pmjay.gov.in or call helpline 14555.\nStep 2: Visit nearest Empanelled Health Care Provider (EHCP) or Common Service Centre (CSC).\nStep 3: Meet the Ayushman Mitra at the helpdesk.\nStep 4: Present Aadhaar Card or Ration Card for biometric e-KYC.\nStep 5: Receive Golden Card / Ayushman Card immediately for cashless treatment.");
        pmjay.setOfficialUrl("https://pmjay.gov.in");
        pmjay.setVerificationStatus("VERIFIED");
        pmjay.setPublicationStatus("PUBLISHED");
        pmjay.setApplicationStatus("OPEN");
        pmjay.setLastVerifiedAt(LocalDate.of(2026, 8, 15));
        pmjay.setInformationSource("National Health Authority (pmjay.gov.in)");

        SchemeEligibilityRule pmjayRule = new SchemeEligibilityRule();
        pmjayRule.setMinimumAge(0);
        pmjayRule.setMaximumAge(120);
        pmjayRule.setMinimumIncome(BigDecimal.ZERO);
        pmjayRule.setMaximumIncome(new BigDecimal("300000"));
        pmjayRule.setEligibleStates("ALL");
        pmjayRule.setEligibleOccupations("Daily wage worker,Agricultural labourer,Farmer,Unemployed,Homemaker,Other");
        pmjayRule.setEligibleSocialCategories("ALL");
        pmjayRule.setEligibleEducationLevels("ALL");
        pmjayRule.setGenderRequirement("ALL");
        pmjayRule.setDisabilityStatus("ALL");
        pmjayRule.setAdditionalConditions("Family annual income typically below ₹3,00,000 or registered in SECC database or holding BPL / Antyodaya Anna Yojana (AAY) ration card.");
        pmjay.setEligibilityRule(pmjayRule);

        pmjay.addDocument(new SchemeDocument("Aadhaar Card or Government Photo ID", "For biometric KYC verification of beneficiary", true));
        pmjay.addDocument(new SchemeDocument("Ration Card (BPL / Food Security Card)", "Proof of family composition and economic status", true));
        pmjay.addDocument(new SchemeDocument("Active Mobile Number", "For verification notifications and Ayushman Card download", true));
        schemeRepository.save(pmjay);

        // 3. Pradhan Mantri Awas Yojana (PMAY)
        Scheme pmay = new Scheme();
        pmay.setSchemeName("Pradhan Mantri Awas Yojana (PMAY - Housing for All)");
        pmay.setSchemeCode("PMAY");
        pmay.setCategory("Housing");
        pmay.setGovernmentLevel("CENTRAL");
        pmay.setDepartment("Ministry of Housing and Urban Affairs & Ministry of Rural Development");
        pmay.setShortDescription("Financial subsidy up to ₹2.67 Lakh for pucca house construction or purchase for economically weaker sections.");
        pmay.setDetailedDescription("Pradhan Mantri Awas Yojana aims to provide pucca houses with basic amenities to all eligible urban and rural families. In urban areas, Credit Linked Subsidy Scheme provides interest subsidy, while beneficiary-led construction provides direct financial grants.");
        pmay.setWhyExists("To ensure every Indian citizen has a permanent safe home with water connection, sanitation facilities, and electricity.");
        pmay.setWhoCanBenefit("Economically Weaker Section (EWS) and Low-Income Group (LIG) families who do not own a pucca house anywhere in India. Female ownership or co-ownership is mandatory.");
        pmay.setBenefits("Interest subsidy up to 6.5% on home loans (worth up to ₹2.67 Lakh) or direct grant assistance of ₹1.20 Lakh to ₹1.50 Lakh for individual house construction.");
        pmay.setApplicationProcess("Step 1: Visit pmaymis.gov.in (Urban) or pmayg.nic.in (Gramin).\nStep 2: Click 'Citizen Assessment' -> 'Apply Online'.\nStep 3: Enter 12-digit Aadhaar number and verify name.\nStep 4: Fill format B application with family income, address, and current housing status.\nStep 5: Upload required documents and note Assessment ID.\nStep 6: Track status on official portal.");
        pmay.setOfficialUrl("https://pmaymis.gov.in");
        pmay.setVerificationStatus("VERIFIED");
        pmay.setPublicationStatus("PUBLISHED");
        pmay.setApplicationStatus("OPEN");
        pmay.setLastVerifiedAt(LocalDate.of(2026, 7, 20));
        pmay.setInformationSource("Ministry of Housing and Urban Affairs (pmaymis.gov.in)");

        SchemeEligibilityRule pmayRule = new SchemeEligibilityRule();
        pmayRule.setMinimumAge(18);
        pmayRule.setMaximumAge(75);
        pmayRule.setMinimumIncome(BigDecimal.ZERO);
        pmayRule.setMaximumIncome(new BigDecimal("600000"));
        pmayRule.setEligibleStates("ALL");
        pmayRule.setEligibleOccupations("Daily wage worker,Agricultural labourer,Farmer,Self-employed,Small business owner,Private employee,Unemployed,Other");
        pmayRule.setEligibleSocialCategories("ALL");
        pmayRule.setEligibleEducationLevels("ALL");
        pmayRule.setGenderRequirement("ALL");
        pmayRule.setDisabilityStatus("ALL");
        pmayRule.setAdditionalConditions("Applicant family must not own any pucca house in their name anywhere in India. Woman head of household must be applicant or co-applicant.");
        pmay.setEligibilityRule(pmayRule);

        pmay.addDocument(new SchemeDocument("Aadhaar Card of all family members", "Identification and verification across government databases", true));
        pmay.addDocument(new SchemeDocument("Income Certificate / Salary Slip / ITR", "Proof that annual family income is within eligible limit (EWS/LIG)", true));
        pmay.addDocument(new SchemeDocument("Proof of Land / House site (if self-constructing)", "Document proving ownership of plot for house construction", true));
        pmay.addDocument(new SchemeDocument("Bank Account Details", "Passbook copy with IFSC code for subsidy deposit", true));
        pmay.addDocument(new SchemeDocument("Affidavit of No Pucca House", "Notarized self-declaration confirming no pucca house ownership", true));
        schemeRepository.save(pmay);

        // 4. Rythu Bharosa (Andhra Pradesh)
        Scheme rythu = new Scheme();
        rythu.setSchemeName("YSR Rythu Bharosa - PM KISAN (Andhra Pradesh)");
        rythu.setSchemeCode("YSR-RYTHU-BHAROSA");
        rythu.setCategory("Agriculture");
        rythu.setGovernmentLevel("STATE");
        rythu.setDepartment("Department of Agriculture, Government of Andhra Pradesh");
        rythu.setShortDescription("Annual financial investment support of ₹13,500 per farmer family in Andhra Pradesh for agricultural operations.");
        rythu.setDetailedDescription("Under YSR Rythu Bharosa, the Government of Andhra Pradesh provides ₹13,500 per year to eligible farmer families (including tenant farmers and ROFR cultivators) in three seasonal installments to meet seed, fertilizer, and operational costs.");
        rythu.setWhyExists("To relieve farmers from high-interest private debt and ensure timely capital for Kharif and Rabi crop seasons.");
        rythu.setWhoCanBenefit("Farmer families residing in Andhra Pradesh who cultivate agricultural lands, including SC, ST, BC, and Minority tenant farmers.");
        rythu.setBenefits("₹13,500 per year per eligible farmer family paid across three tranches (₹7,500 in May/June, ₹4,000 in October, ₹2,000 in January).");
        rythu.setApplicationProcess("Step 1: Visit Village Secretariat (Grama Sachivalayam) or online portal ysrrythubharosa.ap.gov.in.\nStep 2: Check name in Social Audit provisional list displayed at Sachivalayam.\nStep 3: If not registered, submit application with Pattadar passbook, Aadhaar, and CCRC (for tenant farmers).\nStep 4: Village Agriculture Assistant (VAA) verifies land records.\nStep 5: Eligible beneficiaries receive DBT directly into Aadhaar-linked accounts.");
        rythu.setOfficialUrl("https://ysrrythubharosa.ap.gov.in");
        rythu.setVerificationStatus("VERIFIED");
        rythu.setPublicationStatus("PUBLISHED");
        rythu.setApplicationStatus("OPEN");
        rythu.setLastVerifiedAt(LocalDate.of(2026, 8, 10));
        rythu.setInformationSource("Government of Andhra Pradesh (ysrrythubharosa.ap.gov.in)");

        SchemeEligibilityRule rythuRule = new SchemeEligibilityRule();
        rythuRule.setMinimumAge(18);
        rythuRule.setMaximumAge(99);
        rythuRule.setMinimumIncome(BigDecimal.ZERO);
        rythuRule.setMaximumIncome(new BigDecimal("500000"));
        rythuRule.setEligibleStates("Andhra Pradesh");
        rythuRule.setEligibleOccupations("Farmer,Agricultural labourer");
        rythuRule.setEligibleSocialCategories("ALL");
        rythuRule.setEligibleEducationLevels("ALL");
        rythuRule.setGenderRequirement("ALL");
        rythuRule.setDisabilityStatus("ALL");
        rythuRule.setAdditionalConditions("Must be a resident of Andhra Pradesh. Land ownership or valid Crop Cultivator Rights Card (CCRC) for tenant farmers is required.");
        rythu.setEligibilityRule(rythuRule);

        rythu.addDocument(new SchemeDocument("Aadhaar Card", "Mandatory for DBT authentication", true));
        rythu.addDocument(new SchemeDocument("Pattadar Passbook / 1B Adangal (or CCRC for Tenants)", "Proof of land ownership or tenancy agreement in AP", true));
        rythu.addDocument(new SchemeDocument("Bank Passbook", "Active Aadhaar-linked bank account", true));
        rythu.addDocument(new SchemeDocument("White Ration Card (AP Rice Card)", "Proof of local residence and social eligibility", true));
        schemeRepository.save(rythu);

        // 5. Sukanya Samriddhi Yojana (SSY)
        Scheme ssy = new Scheme();
        ssy.setSchemeName("Sukanya Samriddhi Yojana (SSY)");
        ssy.setSchemeCode("SSY");
        ssy.setCategory("Financial Services");
        ssy.setGovernmentLevel("CENTRAL");
        ssy.setDepartment("Department of Posts / Ministry of Finance");
        ssy.setShortDescription("Government-backed savings scheme for girl child with 8.2% annual interest and complete tax exemption under Section 80C.");
        ssy.setDetailedDescription("Sukanya Samriddhi Yojana is a small deposit scheme backed by the Government of India launched as part of 'Beti Bachao Beti Padhao'. Parents or legal guardians can open an account in the name of a girl child from her birth until she turns 10 years old.");
        ssy.setWhyExists("To secure financial future for girl children, supporting their higher education and marriage expenses while curbing child marriage.");
        ssy.setWhoCanBenefit("Parents or legal guardians of a girl child who is an Indian resident and aged between 0 and 10 years. Maximum two girls per family.");
        ssy.setBenefits("Highest government small-savings interest rate (8.2% p.a., compounded annually). Triple tax exemption (EEE - investment, interest, and maturity are completely tax-free). Account matures after 21 years.");
        ssy.setApplicationProcess("Step 1: Download SSY Account Opening Form from India Post (indiapost.gov.in) or authorized commercial banks.\nStep 2: Attach child birth certificate and parent KYC documents (Aadhaar & PAN).\nStep 3: Visit nearest Post Office or authorized bank branch.\nStep 4: Deposit initial amount (minimum ₹250, maximum ₹1.5 Lakh per year).\nStep 5: Receive SSY Passbook to track deposits and accrued interest.");
        ssy.setOfficialUrl("https://www.indiapost.gov.in");
        ssy.setVerificationStatus("VERIFIED");
        ssy.setPublicationStatus("PUBLISHED");
        ssy.setApplicationStatus("OPEN");
        ssy.setLastVerifiedAt(LocalDate.of(2026, 7, 1));
        ssy.setInformationSource("India Post / Ministry of Finance (indiapost.gov.in)");

        SchemeEligibilityRule ssyRule = new SchemeEligibilityRule();
        ssyRule.setMinimumAge(0);
        ssyRule.setMaximumAge(10);
        ssyRule.setMinimumIncome(BigDecimal.ZERO);
        ssyRule.setMaximumIncome(new BigDecimal("99999999"));
        ssyRule.setEligibleStates("ALL");
        ssyRule.setEligibleOccupations("Student,Other,Homemaker");
        ssyRule.setEligibleSocialCategories("ALL");
        ssyRule.setEligibleEducationLevels("ALL");
        ssyRule.setGenderRequirement("FEMALE");
        ssyRule.setDisabilityStatus("ALL");
        ssyRule.setAdditionalConditions("Must be female child aged 10 years or younger at time of opening. Account opened by biological parents or legal guardians.");
        ssy.setEligibilityRule(ssyRule);

        ssy.addDocument(new SchemeDocument("Birth Certificate of Girl Child", "Proof of date of birth and identity of the girl child", true));
        ssy.addDocument(new SchemeDocument("Identity Proof of Guardian (Aadhaar / Voter ID)", "Proof of identity of parent/guardian opening account", true));
        ssy.addDocument(new SchemeDocument("Address Proof of Guardian", "Utility bill, Aadhaar, or Passport showing residential address", true));
        ssy.addDocument(new SchemeDocument("Passport size photographs", "Recent photos of child and guardian", true));
        schemeRepository.save(ssy);

        // 6. Pradhan Mantri Mudra Yojana (PMMY)
        Scheme mudra = new Scheme();
        mudra.setSchemeName("Pradhan Mantri Mudra Yojana (PMMY - Micro Business Loans)");
        mudra.setSchemeCode("PMMY");
        mudra.setCategory("Employment");
        mudra.setGovernmentLevel("CENTRAL");
        mudra.setDepartment("Department of Financial Services, Ministry of Finance");
        mudra.setShortDescription("Collateral-free loans up to ₹10 Lakh for micro and small enterprises, artisans, traders, and self-employed individuals.");
        mudra.setDetailedDescription("PMMY provides loans up to ₹10 Lakh to non-corporate, non-farm small/micro enterprises. Loans are classified into three tiers: Shishu (loans up to ₹50,000), Kishore (loans ₹50,001 to ₹5 Lakh), and Tarun (loans ₹5,00,001 to ₹10 Lakh).");
        mudra.setWhyExists("To foster self-employment and formalize informal micro-enterprises without demanding burdensome collateral or mortgages.");
        mudra.setWhoCanBenefit("Non-farm micro entrepreneurs, small shopkeepers, fruit/vegetable vendors, artisans, transport operators, and small manufacturing units.");
        mudra.setBenefits("Collateral-free business credit up to ₹10 Lakh with affordable interest rates, Mudra RuPay debit card for working capital, and no processing charges for Shishu loans.");
        mudra.setApplicationProcess("Step 1: Prepare business plan and quotation for machinery or inventory.\nStep 2: Apply online on Udyamimitra portal (udyamimitra.in) or visit any commercial/RRB/cooperative bank.\nStep 3: Fill Mudra loan application form.\nStep 4: Submit business proof, identity & address proofs, and quotation.\nStep 5: Bank sanctions loan and issues Mudra Card upon verification.");
        mudra.setOfficialUrl("https://www.mudra.org.in");
        mudra.setVerificationStatus("VERIFIED");
        mudra.setPublicationStatus("PUBLISHED");
        mudra.setApplicationStatus("OPEN");
        mudra.setLastVerifiedAt(LocalDate.of(2026, 6, 15));
        mudra.setInformationSource("Mudra Agency / Ministry of Finance (mudra.org.in)");

        SchemeEligibilityRule mudraRule = new SchemeEligibilityRule();
        mudraRule.setMinimumAge(18);
        mudraRule.setMaximumAge(65);
        mudraRule.setMinimumIncome(BigDecimal.ZERO);
        mudraRule.setMaximumIncome(new BigDecimal("2000000"));
        mudraRule.setEligibleStates("ALL");
        mudraRule.setEligibleOccupations("Small business owner,Self-employed,Daily wage worker,Unemployed,Other");
        mudraRule.setEligibleSocialCategories("ALL");
        mudraRule.setEligibleEducationLevels("ALL");
        mudraRule.setGenderRequirement("ALL");
        mudraRule.setDisabilityStatus("ALL");
        mudraRule.setAdditionalConditions("Applicant must not be a defaulter with any financial institution or bank. Non-farm income generating activity only.");
        mudra.setEligibilityRule(mudraRule);

        mudra.addDocument(new SchemeDocument("Identity Proof (Aadhaar / Voter ID / PAN)", "Official identification document", true));
        mudra.addDocument(new SchemeDocument("Business Address Proof & Registration / Udyam Certificate", "Proof of business enterprise location or registration", true));
        mudra.addDocument(new SchemeDocument("Bank Statement (Last 6 Months)", "Demonstrates transaction history", true));
        mudra.addDocument(new SchemeDocument("Quotation / Estimate of Machinery or Stock", "Proof of purpose for which loan capital is sought", true));
        schemeRepository.save(mudra);

        // 7. Post-Matric Scholarship
        Scheme scholarship = new Scheme();
        scholarship.setSchemeName("Post-Matric Scholarship for SC, ST & OBC Students");
        scholarship.setSchemeCode("POST-MATRIC-SCHOLARSHIP");
        scholarship.setCategory("Education");
        scholarship.setGovernmentLevel("CENTRAL");
        scholarship.setDepartment("Ministry of Social Justice and Empowerment / Ministry of Tribal Affairs");
        scholarship.setShortDescription("Complete tuition fee reimbursement and monthly maintenance allowance for students pursuing post-matriculation and college degrees.");
        scholarship.setDetailedDescription("Centrally sponsored scheme that covers compulsory non-refundable fees (tuition, exam fees) and living allowance for SC, ST, and OBC students pursuing higher secondary, diploma, undergraduate, and postgraduate studies.");
        scholarship.setWhyExists("To increase gross enrollment ratio in higher education among underprivileged communities and eliminate financial barriers.");
        scholarship.setWhoCanBenefit("Students belonging to SC, ST, or OBC communities studying in recognized post-matric courses with parental annual income not exceeding ₹2,50,000.");
        scholarship.setBenefits("100% reimbursement of college tuition fees paid directly to institution or beneficiary, plus monthly maintenance allowance up to ₹1,200/month.");
        scholarship.setApplicationProcess("Step 1: Visit National Scholarship Portal (scholarships.gov.in).\nStep 2: Register as new student with Aadhaar number.\nStep 3: Fill scholarship form with academic admission details and bank details.\nStep 4: Upload Caste Certificate, Income Certificate, and Fee Receipt.\nStep 5: Institute verifies application online.\nStep 6: Scholarship disbursed directly to bank account via DBT.");
        scholarship.setOfficialUrl("https://scholarships.gov.in");
        scholarship.setVerificationStatus("VERIFIED");
        scholarship.setPublicationStatus("PUBLISHED");
        scholarship.setApplicationStatus("OPEN");
        scholarship.setLastVerifiedAt(LocalDate.of(2026, 8, 1));
        scholarship.setInformationSource("National Scholarship Portal (scholarships.gov.in)");

        SchemeEligibilityRule scholarshipRule = new SchemeEligibilityRule();
        scholarshipRule.setMinimumAge(15);
        scholarshipRule.setMaximumAge(35);
        scholarshipRule.setMinimumIncome(BigDecimal.ZERO);
        scholarshipRule.setMaximumIncome(new BigDecimal("250000"));
        scholarshipRule.setEligibleStates("ALL");
        scholarshipRule.setEligibleOccupations("Student");
        scholarshipRule.setEligibleSocialCategories("SC,ST,OBC");
        scholarshipRule.setEligibleEducationLevels("Intermediate / Higher Secondary,Diploma,Undergraduate,Postgraduate");
        scholarshipRule.setGenderRequirement("ALL");
        scholarshipRule.setDisabilityStatus("ALL");
        scholarshipRule.setAdditionalConditions("Applicant must be enrolled in recognized college/university. Annual parental income must not exceed ₹2,50,000.");
        scholarship.setEligibilityRule(scholarshipRule);

        scholarship.addDocument(new SchemeDocument("Caste Certificate", "Issued by competent revenue authority (Tahsildar / MRO)", true));
        scholarship.addDocument(new SchemeDocument("Income Certificate (Valid for current financial year)", "Proof of family income below ₹2.5 Lakh", true));
        scholarship.addDocument(new SchemeDocument("Marksheets of Previous Qualifying Examination", "Proof of passing matriculation / 10th standard", true));
        scholarship.addDocument(new SchemeDocument("College Fee Receipt & Bonafide Certificate", "Proof of current admission in recognized institution", true));
        scholarship.addDocument(new SchemeDocument("Aadhaar-Linked Bank Passbook", "For direct scholarship benefit transfer", true));
        schemeRepository.save(scholarship);

        // 8. MGNREGS
        Scheme mgnregs = new Scheme();
        mgnregs.setSchemeName("Mahatma Gandhi National Rural Employment Guarantee Scheme (MGNREGS)");
        mgnregs.setSchemeCode("MGNREGS");
        mgnregs.setCategory("Social Welfare");
        mgnregs.setGovernmentLevel("CENTRAL");
        mgnregs.setDepartment("Ministry of Rural Development");
        mgnregs.setShortDescription("Legal guarantee of 100 days of paid wage employment per financial year for rural adult household members willing to do manual work.");
        mgnregs.setDetailedDescription("MGNREGS is a social security measure that guarantees the right to work. It provides at least 100 days of guaranteed wage employment in a financial year to every rural household whose adult members volunteer to do unskilled manual work.");
        mgnregs.setWhyExists("To enhance livelihood security in rural India by generating durable public assets (water conservation, rural roads, canals) and supporting rural wages.");
        mgnregs.setWhoCanBenefit("Any adult member (18+ years) residing in a rural area who is willing to do unskilled manual work.");
        mgnregs.setBenefits("Guaranteed wage employment for 100 days at notified statutory wage rates (₹240-₹360/day depending on state). If work is not provided within 15 days of demand, unemployment allowance is legally mandated.");
        mgnregs.setApplicationProcess("Step 1: Submit written application or oral request to Gram Panchayat for Job Card.\nStep 2: Gram Panchayat verifies local residence and issues Job Card within 15 days.\nStep 3: Submit dated application for work to Gram Panchayat or Programme Officer.\nStep 4: Receive work allocation within 5 km of residence within 15 days.\nStep 5: Wages disbursed directly to Aadhaar-seeded bank or post office account weekly/fortnightly.");
        mgnregs.setOfficialUrl("https://nrega.nic.in");
        mgnregs.setVerificationStatus("VERIFIED");
        mgnregs.setPublicationStatus("PUBLISHED");
        mgnregs.setApplicationStatus("OPEN");
        mgnregs.setLastVerifiedAt(LocalDate.of(2026, 8, 20));
        mgnregs.setInformationSource("Ministry of Rural Development (nrega.nic.in)");

        SchemeEligibilityRule mgnregsRule = new SchemeEligibilityRule();
        mgnregsRule.setMinimumAge(18);
        mgnregsRule.setMaximumAge(75);
        mgnregsRule.setMinimumIncome(BigDecimal.ZERO);
        mgnregsRule.setMaximumIncome(new BigDecimal("300000"));
        mgnregsRule.setEligibleStates("ALL");
        mgnregsRule.setEligibleOccupations("Daily wage worker,Agricultural labourer,Farmer,Unemployed,Homemaker,Other");
        mgnregsRule.setEligibleSocialCategories("ALL");
        mgnregsRule.setEligibleEducationLevels("ALL");
        mgnregsRule.setGenderRequirement("ALL");
        mgnregsRule.setDisabilityStatus("ALL");
        mgnregsRule.setAdditionalConditions("Must reside in rural village area and be willing to perform unskilled physical manual work.");
        mgnregs.setEligibilityRule(mgnregsRule);

        mgnregs.addDocument(new SchemeDocument("Aadhaar Card", "Identity verification and biometric attendance tracking", true));
        mgnregs.addDocument(new SchemeDocument("Ration Card / Proof of Rural Residence", "Proof of residence in Gram Panchayat jurisdiction", true));
        mgnregs.addDocument(new SchemeDocument("Bank / Post Office Account Passbook", "For weekly direct wage transfers", true));
        mgnregs.addDocument(new SchemeDocument("Passport Size Photograph", "For MGNREGS Job Card", true));
        schemeRepository.save(mgnregs);

        log.info("Successfully seeded 8 verified Indian government schemes into database.");
    }
}
