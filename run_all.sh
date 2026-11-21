#!/usr/bin/env bash
set -e

# ==============================================================================
# Master Execution & Verification Suite: Assignment 4 (CO4)
# Event-Driven GUI Design, AWT/Swing Architecture & GUI-JDBC Integration
#
# Student Name: Korasika Sudheer
# Register Number: 192324313
# Course: Programming in Java (CSA09)
# ==============================================================================

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_CP=".:$BASE_DIR/lib/*"

echo "=============================================================================="
echo "  STARTING COMPLETE EXECUTION OF ASSIGNMENT 4 (CO4)                           "
echo "  Student: Korasika Sudheer | Register No: 192324313                          "
echo "=============================================================================="

# ------------------------------------------------------------------------------
# Task 1: GUI Design Review
# ------------------------------------------------------------------------------
echo ""
echo ">>> [1/6] Executing Task 1: GUI Design Review (Diagnose & Repair)..."
cd "$BASE_DIR/Task01"
javac -cp "$LIB_CP" FeeWindowRedesign.java
java -Djava.awt.headless=true -cp "$LIB_CP" FeeWindowRedesign

# ------------------------------------------------------------------------------
# Task 2: Hospital Appointment Desk Event-Driven Design
# ------------------------------------------------------------------------------
echo ""
echo ">>> [2/6] Executing Task 2: Hospital Outpatient Appointment Desk..."
cd "$BASE_DIR/Task02"
javac AppointmentDeskApp.java
java AppointmentDeskApp

# ------------------------------------------------------------------------------
# Task 3: Campus Library Layout Design
# ------------------------------------------------------------------------------
echo ""
echo ">>> [3/6] Executing Task 3: Campus Library Search Screen..."
cd "$BASE_DIR/Task03"
javac LibrarySearchScreen.java
java LibrarySearchScreen

# ------------------------------------------------------------------------------
# Task 4: Event-Handling Forensics (DrawPad)
# ------------------------------------------------------------------------------
echo ""
echo ">>> [4/6] Executing Task 4: DrawPad Event-Handling Forensics..."
cd "$BASE_DIR/Task04"
javac DrawPad.java
java DrawPad

# ------------------------------------------------------------------------------
# Task 5: Same Screen Different Event-Handling Styles (Converter)
# ------------------------------------------------------------------------------
echo ""
echo ">>> [5/6] Executing Task 5: Currency Converter Event-Handling Styles..."
cd "$BASE_DIR/Task05"
javac Converter.java ConverterStylesComparison.java
java Converter
java ConverterStylesComparison

# ------------------------------------------------------------------------------
# Task 6: Design Challenge (Campus Event Registration Desktop App)
# ------------------------------------------------------------------------------
echo ""
echo ">>> [6/6] Executing Task 6: Campus Event Registration App (Swing + JDBC)..."
cd "$BASE_DIR/Task06"
javac -cp "$LIB_CP" CampusEventRegistrationApp.java
java -cp "$LIB_CP" CampusEventRegistrationApp

echo ""
echo "=============================================================================="
echo "  ASSIGNMENT 4 VERIFICATION COMPLETE: ALL 6 TASKS PASSED WITH ZERO ERRORS!    "
echo "=============================================================================="
