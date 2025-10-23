package com.example.universal_ac_tv_remote.utils;

import java.util.UUID;

public class Constants {

    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String ESP32_MAC_ADDRESS = "B0:B2:1C:44:C0:CE";
    // Enum unutar klase
    public enum DecodeType {
        UNKNOWN(-1),
        UNUSED(0),
        RC5(1),
        RC6(2),
        NEC(3),
        SONY(4),
        PANASONIC(5),
        JVC(6),
        SAMSUNG(7),
        WHYNTER(8),
        AIWA_RC_T501(9),
        LG(10),
        SANYO(11),
        MITSUBISHI(12),
        DISH(13),
        SHARP(14),
        COOLIX(15),
        DAIKIN(16),
        DENON(17),
        KELVINATOR(18),
        SHERWOOD(19),
        MITSUBISHI_AC(20),
        RCMM(21),
        SANYO_LC7461(22),
        RC5X(23),
        GREE(24),
        PRONTO(25),
        NEC_LIKE(26),
        ARGO(27),
        TROTEC(28),
        NIKAI(29),
        RAW(30),
        GLOBALCACHE(31),
        TOSHIBA_AC(32),
        FUJITSU_AC(33),
        MIDEA(34),
        MAGIQUEST(35),
        LASERTAG(36),
        CARRIER_AC(37),
        HAIER_AC(38),
        MITSUBISHI2(39),
        HITACHI_AC(40),
        HITACHI_AC1(41),
        HITACHI_AC2(42),
        GICABLE(43),
        HAIER_AC_YRW02(44),
        WHIRLPOOL_AC(45),
        SAMSUNG_AC(46),
        LUTRON(47),
        ELECTRA_AC(48),
        PANASONIC_AC(49),
        PIONEER(50),
        LG2(51),
        MWM(52),
        DAIKIN2(53),
        VESTEL_AC(54),
        TECO(55),
        SAMSUNG36(56),
        TCL112AC(57),
        LEGOPF(58),
        MITSUBISHI_HEAVY_88(59),
        MITSUBISHI_HEAVY_152(60),
        DAIKIN216(61),
        SHARP_AC(62),
        GOODWEATHER(63),
        INAX(64),
        DAIKIN160(65),
        NEOCLIMA(66),
        DAIKIN176(67),
        DAIKIN128(68),
        AMCOR(69),
        DAIKIN152(70),
        MITSUBISHI136(71),
        MITSUBISHI112(72),
        HITACHI_AC424(73),
        SONY_38K(74),
        EPSON(75),
        SYMPHONY(76),
        HITACHI_AC3(77),
        DAIKIN64(78),
        AIRWELL(79),
        DELONGHI_AC(80),
        DOSHISHA(81),
        MULTIBRACKETS(82),
        CARRIER_AC40(83),
        CARRIER_AC64(84),
        HITACHI_AC344(85),
        CORONA_AC(86),
        MIDEA24(87),
        ZEPEAL(88),
        SANYO_AC(89),
        VOLTAS(90),
        METZ(91),
        TRANSCOLD(92),
        TECHNIBEL_AC(93),
        MIRAGE(94),
        ELITESCREENS(95),
        PANASONIC_AC32(96),
        MILESTAG2(97),
        ECOCLIM(98),
        XMP(99),
        TRUMA(100),
        HAIER_AC176(101),
        TEKNOPOINT(102),
        KELON(103),
        TROTEC_3550(104),
        SANYO_AC88(105),
        BOSE(106),
        ARRIS(107),
        RHOSS(108),
        AIRTON(109),
        COOLIX48(110),
        HITACHI_AC264(111),
        KELON168(112),
        HITACHI_AC296(113),
        DAIKIN200(114),
        HAIER_AC160(115),
        CARRIER_AC128(116),
        TOTO(117),
        CLIMABUTLER(118),
        TCL96AC(119),
        BOSCH144(120),
        SANYO_AC152(121),
        DAIKIN312(122),
        GORENJE(123),
        WOWWEE(124),
        CARRIER_AC84(125),
        YORK(126),
        BLUESTARHEAVY(127);

        private final int value;

        DecodeType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        // Metoda za pronalaženje enum-a po vrednosti
        public static DecodeType fromValue(int value) {
            for (DecodeType type : DecodeType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown decode type value: " + value);
        }

        // Metoda za poslednji tip (ekvivalent kLastDecodeType)
        public static DecodeType getLastDecodeType() {
            return BLUESTARHEAVY;
        }
    }
}
