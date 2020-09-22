%% http://lsr.di.unimi.it/LSR/Item?id=710
%% see also http://lilypond.org/doc/v2.18/Documentation/notation/formatting-text

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% A function to create Roman numerals for harmonic analysis.
%%
%% Syntax: \markup \rN { ...list of symbols... }
%%
%% List symbols in this order (as needed): Roman numeral, quality, top number of
%% inversion symbol, bottom number, "/" (if secondary function), Roman numeral.
%%
%% "bVII" creates flat VII; "svi" creates sharp vi; "Ab" creates A-flat; "As" A-sharp
%%
%% Qualities: use "o" for diminished, "h" for half-diminished,
%% "+" for augmented, "b" for flat.  Use any combination of "M" and "m":
%% M, m, MM7, Mm, mm, Mmm9, etc. Added-note chords: add, add6, etc.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

#(define rN-size -1) %% change to vary size of numerals

#(define scaling (magstep rN-size))

%%% change constant to adjust distance between characters
#(define X-separation (* scaling 0.2))

%%% symmetrical distance between inversion figures and midline
#(define inversion-Y-separation (* scaling 0.1))

#(define dim
   (markup
    #:override `(thickness . ,scaling)
    #:draw-circle (* scaling 0.25) (* scaling 0.1) #f))

#(define half-dim
   (markup
    #:override `(thickness . ,scaling)
    #:combine
    (#:combine dim
               #:draw-line `(,(* scaling -0.3) . ,(* scaling -0.3)))
    #:draw-line `(,(* scaling 0.3) . ,(* scaling 0.3))))

#(define augmented
   (markup
    #:override `(thickness . ,scaling)
    #:combine
    (#:combine #:draw-line `(,(* scaling -0.25) . 0)
               #:draw-line `(0 . ,(* scaling -0.25)))
    (#:combine #:draw-line `(,(* scaling 0.25) . 0)
               #:draw-line `(0 . ,(* scaling 0.25)))))

#(define (acc? str num) (string? number?)
   (eq? num (string-index str (char-set #\b #\s #\n)))) %% checks for accidental

#(define acc `((#\b . ,(markup #:flat))
               (#\s . ,(markup #:sharp))
               (#\n . ,(markup #:natural))))

#(define-markup-command (rN layout props symbols) (markup-list?)
   ;; isolate and normalize segment of list before slash (if any)
   (let* ((up-to-slash (car (split-list-by-separator symbols (lambda (x) (equal? x "/")))))
          (first-part (append up-to-slash (make-list (- 4 (length up-to-slash)) "")))
          (normalized
           (if (or (string-index (cadr first-part) (string->char-set "mMaAdD"))
                   (not (null? (lset-intersection equal? '("o" "h" "+" "b") (cdr first-part)))))
               first-part
               (list (car first-part) "" (cadr first-part) (caddr first-part))))
          (base (car normalized))
          (quality (cadr normalized))
          (quality-marker
           (cond ((equal? "o" quality) (markup #:raise (* 0.5 scaling) dim))
                 ((equal? "h" quality) (markup #:raise (* 0.5 scaling) half-dim))
                 ((equal? "+" quality) (markup #:raise (* 0.5 scaling) augmented))
                 ((equal? "b" quality) (markup #:raise (* 0.5 scaling) #:flat))
                 ((equal? "" quality) (markup #:null))
                 (else (markup quality))))
          (upper (caddr normalized))
          (lower (cadddr normalized))
          ;; isolate slash and what follows
          (second-part (if (member "/" symbols) (member "/" symbols) '("" "")))
          (rN-two (cadr second-part))
          (base-stencil
           (interpret-markup layout
                             (cons (list `(word-space . ,X-separation) `(font-size . ,rN-size)) props)
                             (markup base)))
          ;; calculate Y midpoint of base (for positioning quality and inversion)
          (vertical-offset (/ (interval-length (ly:stencil-extent base-stencil Y)) 2))
          (inversion-stencil
           (if (equal? lower "")
               (ly:stencil-translate-axis (interpret-markup layout
                                                            (cons (list `(word-space . ,X-separation)) props)
                                                            (markup #:fontsize (- rN-size 5) upper))
                                          inversion-Y-separation Y)

               (ly:stencil-aligned-to
                (ly:stencil-combine-at-edge
                 (interpret-markup layout
                                   (cons (list `(word-space . ,X-separation)) props)
                                   (markup #:fontsize (- rN-size 5) upper))
                 Y DOWN
                 (interpret-markup layout
                                   (cons (list `(word-space . ,X-separation)) props)
                                   (markup #:fontsize (- rN-size 5) lower))
                 (* 2 inversion-Y-separation))
                Y CENTER)))
          (quality-marker-stencil
           (ly:stencil-translate-axis (interpret-markup layout
                                                        (cons (list `(word-space . ,X-separation)) props)
                                                        (markup #:fontsize (- rN-size 5) quality-marker))
                                      inversion-Y-separation Y))

          ;; base, quality marker, and inversion
          (one
           (ly:stencil-combine-at-edge

            (ly:stencil-combine-at-edge
             (interpret-markup layout
                               (cons (list `(word-space . ,X-separation)) props)
                               ;; accommodates an accidental either before or after
                               (cond ((acc? base 0)
                                      (markup #:fontsize (- rN-size 4)
                                              #:raise (* 2 vertical-offset) #:vcenter
                                              (assoc-ref acc (string-ref base 0))
                                              #:fontsize rN-size (substring base 1)))

                                     ((acc? base (1- (string-length base)))
                                      (markup #:fontsize rN-size
                                              (substring base 0 (1- (string-length base)))
                                              #:fontsize (- rN-size 4) #:raise (/ scaling 2)
                                              (assoc-ref acc (string-ref base (1- (string-length base))))))

                                     (else (markup #:fontsize rN-size base))))
             X RIGHT
             (ly:stencil-translate-axis quality-marker-stencil vertical-offset Y)
             (if (equal? "" quality) 0 X-separation))

            X RIGHT
            (ly:stencil-translate-axis inversion-stencil vertical-offset Y)
            (if (equal? "" upper) 0 X-separation)))

          ;; slash and after
          (two
           (ly:stencil-combine-at-edge
            (interpret-markup layout
                              (cons (list `(word-space . ,X-separation) `(font-size . ,rN-size)) props)
                              (if (equal? "" lower)
                                  (markup (car second-part))
                                  (markup #:hspace X-separation (car second-part))))
            X RIGHT
            (interpret-markup layout
                              (cons (list `(word-space . ,X-separation)) props)
                              (cond ((acc? rN-two 0)
                                     (markup #:fontsize (- rN-size 4)
                                             #:raise scaling (assoc-ref acc (string-ref rN-two 0))
                                             #:fontsize rN-size (substring rN-two 1)))

                                    ((acc? rN-two (1- (string-length rN-two)))
                                     (markup #:fontsize rN-size
                                             (substring rN-two 0 (1- (string-length rN-two)))
                                             #:fontsize (- rN-size 4) #:raise (/ scaling 2)
                                             (assoc-ref acc (string-ref rN-two (1- (string-length rN-two))))))

                                    (else (markup #:fontsize rN-size rN-two))))
            X-separation)))

     (if (equal? rN-two "")
         one
         (ly:stencil-combine-at-edge one X RIGHT two 0))))