package me.dolphin2410.mbti

enum class MBTI {
    INTJ,
    INTP,
    ISTJ,
    ISTP,
    INFJ,
    INFP,
    ISFJ,
    ISFP,
    ENTJ,
    ENTP,
    ESTJ,
    ESTP,
    ENFJ,
    ENFP,
    ESFJ,
    ESFP;

    fun isI(): Boolean {
        return this.name[0] == 'I'
    }

    fun isN(): Boolean {
        return this.name[1] == 'N'
    }

    fun isT(): Boolean {
        return this.name[2] == 'T'
    }

    fun isJ(): Boolean {
        return this.name[3] == 'J'
    }
}